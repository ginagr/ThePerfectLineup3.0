package ggr.tpl7.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.text.ParseException;
import java.util.UUID;

import ggr.tpl7.Athlete;
import ggr.tpl7.database.AthleteDbSchema.AthleteTable;

public class AthleteCursorWrapper extends CursorWrapper {
    public AthleteCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Athlete getAthlete() throws ParseException {
        String uuid = getString(getColumnIndex(AthleteTable.Cols.UUID));
        String firstName = getString(getColumnIndex(AthleteTable.Cols.FIRSTNAME));
        String lastName = getString(getColumnIndex(AthleteTable.Cols.LASTNAME));
        int position = getInt(getColumnIndex(AthleteTable.Cols.POSITION));
        int feet = getInt(getColumnIndex(AthleteTable.Cols.FEET));
        int inches = getInt(getColumnIndex(AthleteTable.Cols.INCHES));
        int weight = getInt(getColumnIndex(AthleteTable.Cols.WEIGHT));
        int twokMin = getInt(getColumnIndex(AthleteTable.Cols.TWOKMIN));
        int twokSec = getInt(getColumnIndex(AthleteTable.Cols.TWOKSEC));

        Athlete athlete = new Athlete(UUID.fromString(uuid), firstName, lastName);
        athlete.setPosition(position);
        athlete.setFeet(feet);
        athlete.setInches(inches);
        athlete.setWeight(weight);
        athlete.setTwokMin(twokMin);
        athlete.setTwokSec(twokSec);

        //TODO: change 2k to time instead of ints

        return athlete;

    }
}
