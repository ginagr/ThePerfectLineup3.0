package ggr.tpl7.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.text.ParseException;
import java.util.UUID;

import ggr.tpl7.model.Athlete;
import ggr.tpl7.database.AthleteDbSchema.AthleteTable;
import ggr.tpl7.model.AthleteLab;

import static ggr.tpl7.model.AthleteLab.formatStringToDate;

public class AthleteCursorWrapper extends CursorWrapper {
    public AthleteCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Athlete getAthlete() throws ParseException {
        String uuid = getString(getColumnIndex(AthleteTable.Cols.UUID));
        String firstName = getString(getColumnIndex(AthleteTable.Cols.FIRSTNAME));
        String lastName = getString(getColumnIndex(AthleteTable.Cols.LASTNAME));
        String position = getString(getColumnIndex(AthleteTable.Cols.POSITION));
        int feet = getInt(getColumnIndex(AthleteTable.Cols.FEET));
        int inches = getInt(getColumnIndex(AthleteTable.Cols.INCHES));
        int weight = getInt(getColumnIndex(AthleteTable.Cols.WEIGHT));
        String twok = getString(getColumnIndex(AthleteTable.Cols.TWOK));
        String contact = getString(getColumnIndex(AthleteTable.Cols.CONTACT));
        int inLineup = getInt(getColumnIndex(AthleteTable.Cols.INLINEUP));
        String boatuuid = getString(getColumnIndex(AthleteTable.Cols.BOATUUID));
        int seat = getInt(getColumnIndex(AthleteTable.Cols.SEAT));

        Athlete athlete = new Athlete(UUID.fromString(uuid), firstName, lastName);
        athlete.setPosition(AthleteLab.toPosition(position));
        athlete.setFeet(feet);
        athlete.setInches(inches);
        athlete.setWeight(weight);
        athlete.setTwok(formatStringToDate(twok));
        athlete.setLinkContact(contact);
        athlete.setInLineup(inLineup != 0);
        athlete.setBoatId(UUID.fromString(boatuuid));
        athlete.setSeat(seat);

        return athlete;

    }
}
