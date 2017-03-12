package ggr.tpl7.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ggr.tpl7.database.AthleteBaseHelper;
import ggr.tpl7.database.AthleteBoatDbSchema;
import ggr.tpl7.database.AthleteCursorWrapper;
import ggr.tpl7.database.AthleteDbSchema;
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
                return new ArrayList<Boat>();
            }
            cursor.moveToNext();
        }
        cursor.close();

        return boats;
    }

    public Boat getBoat(UUID id) {
        BoatCursorWrapper cursor = queryBoat(
                BoatTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getBoat();
        } catch (ParseException e) {
            Log.e("BoatLab", "No boat was found");
            return new Boat();
        } finally {
            cursor.close();
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
        String whereClause = "_id" + "=?";
        Log.d("", "Delete " + boat.getName());
        database.delete(BoatTable.NAME, BoatTable.Cols.UUID + " = ?", new String[]{sid});
    }

    public static BoatSize toBoatSize(String bs){
        if(bs.equals("8+")){
            return BoatSize.EIGHT;
        } else if(bs.equals("4+")){
            return BoatSize.FOUR;
        } else if(bs.equals("4x")){
            return BoatSize.QUAD;
        } else if(bs.equals("2+")){
            return BoatSize.DOUBLE;
        } else if(bs.equals("2x")){
            return BoatSize.PAIR;
        } else if(bs.equals("1x")){
            return BoatSize.SINGLE;
        } else {
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
