package ggr.tpl7.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ggr.tpl7.database.AthleteBaseHelper;
import ggr.tpl7.database.BoatCursorWrapper;
import ggr.tpl7.database.BoatDbSchema.BoatTable;

public class BoatLab {
    private static BoatLab boatLab;

    private Context context;
    private SQLiteDatabase database;

    public static BoatLab get(Context context) {
        if (boatLab == null) {
            boatLab = new BoatLab(context);
        }
        return boatLab;
    }

    private BoatLab(Context context) {
        this.context = context.getApplicationContext();
        database = new AthleteBaseHelper(this.context)
                .getWritableDatabase();
    }


    public void addBoat(Boat c) {
        ContentValues values = getContentValues(c);

        database.insert(BoatTable.NAME, null, values);
    }

    public List<Boat> getBoats(){
        List<Boat> boats = new ArrayList<>();

        BoatCursorWrapper cursor = queryBoat(null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            try {
                boats.add(cursor.getBoat());
            } catch (ParseException e) {
                Log.e("BoatLab", "No boats where found");
                return new ArrayList<>();
            }
            cursor.moveToNext();
        }
        cursor.close();

        return boats;
    }

    public Boat getBoat(UUID id) {

        try (BoatCursorWrapper cursor = queryBoat(
                BoatTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        )) {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getBoat();
        } catch (ParseException e) {
            Log.e("BoatLab", "No boat was found");
            return new Boat();
        }
    }

    public void updateBoat(Boat boat) {
        String uuidString = boat.getId().toString();
        ContentValues values = getContentValues(boat);

        database.update(BoatTable.NAME, values,
                BoatTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private static ContentValues getContentValues(Boat boat) {
        ContentValues values = new ContentValues();
        values.put(BoatTable.Cols.UUID, boat.getId().toString());
        values.put(BoatTable.Cols.BOATSIZE, boat.getBoatSize().toString());
        values.put(BoatTable.Cols.COX, boat.isCox());
        values.put(BoatTable.Cols.NAME, boat.getName());
        values.put(BoatTable.Cols.CURRENT, boat.isCurrent());

        return values;
    }

    private BoatCursorWrapper queryBoat(String whereClause, String[] whereArgs) {
        Cursor cursor = database.query(
                BoatTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy\

        );

        return new BoatCursorWrapper(cursor);
    }

    public void deleteBoat(UUID id){
        Boat boat  = getBoat(id);
        String sid = id.toString();
        Log.d("", "Delete " + boat.getName());
        List<Athlete> athletesInBoat = AthleteLab.get(context).getAthletesByBoat(boat.getId());
        for(Athlete athlete : athletesInBoat){
            athlete.setInLineup(false);
            athlete.setBoatId(null);
            athlete.setPosition(null);
            AthleteLab.get(context).updateAthlete(athlete);
        }
        database.delete(BoatTable.NAME, BoatTable.Cols.UUID + " = ?", new String[]{sid});
    }

    public static BoatSize toBoatSize(String bs){
        switch (bs) {
            case "8+":
                return BoatSize.EIGHT;
            case "4+":
                return BoatSize.FOUR;
            case "4x":
                return BoatSize.QUAD;
            case "2+":
                return BoatSize.DOUBLE;
            case "2x":
                return BoatSize.PAIR;
            case "1x":
                return BoatSize.SINGLE;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void changeCurrentBoat(Boat newBoat){
        List<Boat> boats = getBoats();
        for(int i = 0; i < boats.size(); i++){
            boats.get(i).setCurrent(false);
            updateBoat(boats.get(i));
        }
        newBoat.setCurrent(true);
        updateBoat(newBoat);
    }

}
