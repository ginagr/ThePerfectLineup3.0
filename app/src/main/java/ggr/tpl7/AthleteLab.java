package ggr.tpl7;

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
import java.util.ArrayList;
import java.util.List;
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

    public List<Athlete> getAthletes() throws ParseException {
        List<Athlete> athlete = new ArrayList<>();

        AthleteCursorWrapper cursor = queryAthlete(null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            athlete.add(cursor.getAthlete());
            cursor.moveToNext();
        }
        cursor.close();

        return athlete;
    }

    public Athlete getAthlete(UUID id) throws ParseException {
        AthleteCursorWrapper cursor = queryAthlete(
                AthleteTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getAthlete();
        } finally {
            cursor.close();
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
        values.put(AthleteTable.Cols.POSITION, athlete.getPosition());
        values.put(AthleteTable.Cols.FEET, athlete.getFeet());
        values.put(AthleteTable.Cols.INCHES, athlete.getInches());
        values.put(AthleteTable.Cols.WEIGHT, athlete.getWeight());
        values.put(AthleteTable.Cols.TWOKMIN, athlete.getTwokMin());
        values.put(AthleteTable.Cols.TWOKSEC, athlete.getTwokSec());

        return values;
    }

    private AthleteCursorWrapper queryAthlete(String whereClause, String[] whereArgs) {
        Cursor cursor = database.query(
                AthleteTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy\

        );

        return new AthleteCursorWrapper(cursor);
    }

    public void deleteAthlete(UUID id){
        try {
            Athlete athlete  = getAthlete(id);
            String sid = id.toString();
            String whereClause = "_id" + "=?";
            Log.d("", "Delete " + athlete.getFirstName());
            database.delete(AthleteTable.NAME, AthleteTable.Cols.UUID + " = ?", new String[]{sid});

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
