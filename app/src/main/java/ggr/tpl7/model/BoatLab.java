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

    public List<Boat> getBoats() throws ParseException {
        List<Boat> boats = new ArrayList<>();

        BoatCursorWrapper cursor = queryBoat(null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            boats.add(cursor.getBoat());
            cursor.moveToNext();
        }
        cursor.close();

        return boats;
    }

    public Boat getBoat(UUID id) throws ParseException {
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
        values.put(BoatTable.Cols.BOATSIZE, boat.getBoatSize());
        values.put(BoatTable.Cols.COX, boat.isCox());
        values.put(BoatTable.Cols.NAME, boat.getName());

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
        try {
            Boat boat  = getBoat(id);
            String sid = id.toString();
            String whereClause = "_id" + "=?";
            Log.d("", "Delete " + boat.getName());
            database.delete(BoatTable.NAME, BoatTable.Cols.UUID + " = ?", new String[]{sid});

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
