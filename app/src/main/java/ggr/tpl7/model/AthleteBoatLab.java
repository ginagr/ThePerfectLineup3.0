package ggr.tpl7.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import ggr.tpl7.database.AthleteBaseHelper;
import ggr.tpl7.database.AthleteBoatDbSchema.AthleteBoatTable;

import java.util.UUID;

public class AthleteBoatLab {
    private static AthleteBoatLab athleteBoatLab;

    private Context context;
    private SQLiteDatabase database;

    public static AthleteBoatLab get(Context context) {
        if (athleteBoatLab == null) {
            athleteBoatLab = new AthleteBoatLab(context);
        }
        return athleteBoatLab;
    }

    private AthleteBoatLab(Context context) {
        this.context = context.getApplicationContext();
        database = new AthleteBaseHelper(this.context)
                .getWritableDatabase();
    }

    public void addAthleteBoat(UUID athleteId, UUID boatId, int seat) {
        ContentValues values = getContentValues(athleteId, boatId, seat);

        database.insert(AthleteBoatTable.NAME, null, values);
    }

    public void updateAthleteBoat(UUID athleteId, UUID boatId, int seat) {
        ContentValues values = getContentValues(athleteId, boatId, seat);

        database.update(AthleteBoatTable.NAME, values,
                AthleteBoatTable.Cols.ATHLETEID + " = ?",
                new String[] { athleteId.toString() });
    }

    private static ContentValues getContentValues(UUID athleteId, UUID boatId, int seat) {
        ContentValues values = new ContentValues();
        values.put(AthleteBoatTable.Cols.ATHLETEID, athleteId.toString());
        values.put(AthleteBoatTable.Cols.BOATID, boatId.toString());
        values.put(AthleteBoatTable.Cols.SEAT, seat);

        return values;
    }

    public void deleteAthlete(UUID athleteId){
        String sid = athleteId.toString();
        String whereClause = "_id" + "=?";
        Log.d("AthleteBoatLab", "Deleted AthleteBoatLab " + athleteId);
        database.delete(AthleteBoatTable.NAME, AthleteBoatTable.Cols.ATHLETEID + " = ?", new String[]{sid});
    }
}
