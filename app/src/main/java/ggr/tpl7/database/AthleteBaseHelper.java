package ggr.tpl7.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ggr.tpl7.database.AthleteDbSchema.AthleteTable;
import ggr.tpl7.database.BoatDbSchema.BoatTable;
import ggr.tpl7.database.AthleteBoatDbSchema.AthleteBoatTable;

public class AthleteBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "LineupBase.db";
    private static final String KEY_ID = "id";

    public AthleteBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + AthleteTable.NAME + "(" +
            "_id integer primary key autoincrement," +
            AthleteTable.Cols.UUID + ", " +
            AthleteTable.Cols.FIRSTNAME + ", " +
            AthleteTable.Cols.LASTNAME + ", " +
            AthleteTable.Cols.POSITION + ", " +
            AthleteTable.Cols.FEET + ", " +
            AthleteTable.Cols.INCHES + ", " +
            AthleteTable.Cols.WEIGHT + ", " +
            AthleteTable.Cols.TWOK + ", " +
            AthleteTable.Cols.CONTACT + ", " +
            AthleteTable.Cols.INLINEUP + ", " +
            AthleteTable.Cols.BOATUUID + ", " +
            AthleteTable.Cols.SEAT + ")"
            );

        db.execSQL("create table " + BoatTable.NAME + "(" +
                "_id integer primary key autoincrement," +
                BoatTable.Cols.UUID + ", " +
                BoatTable.Cols.BOATSIZE + ", " +
                BoatTable.Cols.COX + ", " +
                BoatTable.Cols.NAME + ", " +
                BoatTable.Cols.CURRENT + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
