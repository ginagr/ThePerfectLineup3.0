package ggr.tpl7.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ggr.tpl7.database.AthleteDbSchema.AthleteTable;

public class AthleteBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "athleteBase.db";

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
            AthleteTable.Cols.TWOKMIN + ", " +
            AthleteTable.Cols.TWOKSEC + ", " +
            AthleteTable.Cols.CONTACT + ", " +
            AthleteTable.Cols.INLINEUP + ")"
            );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
