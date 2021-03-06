package ggr.tpl7.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import ggr.tpl7.database.AthleteBaseHelper;
import ggr.tpl7.database.AthleteCursorWrapper;
import ggr.tpl7.database.AthleteDbSchema.AthleteTable;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AthleteLab {
    private static AthleteLab athleteLab;

    private Context context;
    private SQLiteDatabase database;

    public static AthleteLab get(Context context) {
        if (athleteLab == null) {
            athleteLab = new AthleteLab(context);
        }
        return athleteLab;
    }

    private AthleteLab(Context context) {
        this.context = context.getApplicationContext();
        database = new AthleteBaseHelper(this.context)
                .getWritableDatabase();
    }


    public void addAthlete(Athlete c) {
        ContentValues values = getContentValues(c);

        database.insert(AthleteTable.NAME, null, values);
    }

    public List<Athlete> getAthletes(String order){
        List<Athlete> athlete = new ArrayList<>();

        AthleteCursorWrapper cursor = queryAthlete(null, null, order);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            try {
                athlete.add(cursor.getAthlete());
            } catch (ParseException e) {
                Log.e("AthleteLab", "Could not get list of athletes");
                return new ArrayList<>();
            }
            cursor.moveToNext();
        }
        cursor.close();

        return athlete;
    }

    public Athlete getAthlete(UUID id){

        try (AthleteCursorWrapper cursor = queryAthlete(
                AthleteTable.Cols.UUID + " = ?",
                new String[]{id.toString()},
                null
        )) {
            if (cursor.getCount() == 0) {
                Log.e("AthleteLab", "Could not find athlete with id " + id);
                return new Athlete();
            }

            cursor.moveToFirst();
            return cursor.getAthlete();
        } catch (ParseException e) {
            Log.e("AthleteLab", "Could not find athlete with id: " + id);
            e.printStackTrace();
            return new Athlete();
        }
    }

    public File[] getPhotoFiles(Athlete athlete) {
        File externalFilesDir = context
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        String[] photoFileNames = athlete.getPhotoFilenames();
        int numberOfPossiblePhotos = photoFileNames.length;
        File[] photoFiles = new File[numberOfPossiblePhotos];

        for (int i = 0; i < numberOfPossiblePhotos; i++)
            photoFiles[i] = new File(externalFilesDir, photoFileNames[i]);

        return photoFiles;
    }


    public void updateAthlete(Athlete athlete) {
        String uuidString = athlete.getId().toString();
        ContentValues values = getContentValues(athlete);

        database.update(AthleteTable.NAME, values,
                AthleteTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private static ContentValues getContentValues(Athlete athlete) {
        ContentValues values = new ContentValues();
        values.put(AthleteTable.Cols.UUID, athlete.getId().toString());
        values.put(AthleteTable.Cols.FIRSTNAME, athlete.getFirstName());
        values.put(AthleteTable.Cols.LASTNAME, athlete.getLastName());
        values.put(AthleteTable.Cols.POSITION, athlete.getPosition().toString());
        values.put(AthleteTable.Cols.FEET, athlete.getFeet());
        values.put(AthleteTable.Cols.INCHES, athlete.getInches());
        values.put(AthleteTable.Cols.WEIGHT, athlete.getWeight());
        values.put(AthleteTable.Cols.TWOK, formatDateToString(athlete.getTwok()));
        values.put(AthleteTable.Cols.CONTACT, athlete.getLinkContact());
        values.put(AthleteTable.Cols.INLINEUP, athlete.getInLineup());
        values.put(AthleteTable.Cols.BOATUUID, athlete.getBoatId().toString());
        values.put(AthleteTable.Cols.SEAT, athlete.getSeat());

        return values;
    }

    private AthleteCursorWrapper queryAthlete(String whereClause, String[] whereArgs, String orderBy) {
        Cursor cursor = database.query(
                    AthleteTable.NAME,
                    null, // Columns - null selects all columns
                    whereClause,
                    whereArgs,
                    null, // groupBy
                    null, // having
                    orderBy  // orderBy\

            );

        return new AthleteCursorWrapper(cursor);
    }

    public void deleteAthlete(UUID id){
        Athlete athlete  = getAthlete(id);
        String sid = id.toString();
       // String whereClause = "_id" + "=?";
        Log.d("", "Delete " + athlete.getFirstName());
        database.delete(AthleteTable.NAME, AthleteTable.Cols.UUID + " = ?", new String[]{sid});
    }

    public void deleteAthletes(){
        Log.d("", "Deleting all athletes");
        List<Athlete> tempAthlete = getAthletes(null);
        for(int i = 0; i < tempAthlete.size(); i++){
            deleteAthlete(tempAthlete.get(i).getId());
        }
    }

    public List<Athlete> getAthletesByBoat(UUID boatID){
        List<Athlete> athletes;
        List<Athlete> boatAthletes = new ArrayList<>();
        Log.e("AthleteLab", "Getting athletes from boat " + boatID);

        athletes = getAthletes(null);
        for(int i = 0; i < athletes.size(); i++){
            if(athletes.get(i).getBoatId().equals(boatID)){
                boatAthletes.add(athletes.get(i));
            }
        }
        return boatAthletes;
    }

    public static Position toPosition(String pos){
        switch (pos) {
            case "Coxswain":
                return Position.COXSWAIN;
            case "Both":
                return Position.BOTH;
            case "Port":
                return Position.PORT;
            case "Starboard":
                return Position.STARBOARD;
            default:
                return Position.NONE;
        }
    }

    public static Date formatStringToDate(String date){
        if(date == null || date.isEmpty()){
            return null;
        }
        try {
            return new SimpleDateFormat("mm:ss.S", Locale.US).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatDateToString(Date date){
        if(date == null){
            return "";
        }

        SimpleDateFormat ft = new SimpleDateFormat("mm:ss.S", Locale.US);
        try {
            return ft.format(date);

        } catch (Exception e) {
            Log.e("AthleteLab", "Cannot format date " + date.toString() );
           return "";
        }
    }

}
