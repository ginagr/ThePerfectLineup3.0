package ggr.tpl7.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.text.ParseException;
import java.util.UUID;

import ggr.tpl7.database.BoatDbSchema.BoatTable;
import ggr.tpl7.model.Boat;
import ggr.tpl7.model.BoatLab;

public class BoatCursorWrapper extends CursorWrapper {
    public BoatCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Boat getBoat() throws ParseException {
        String uuid = getString(getColumnIndex(BoatTable.Cols.UUID));
        String size = getString(getColumnIndex(BoatTable.Cols.BOATSIZE));
        int cox = getInt(getColumnIndex(BoatTable.Cols.COX));
        String name = getString(getColumnIndex(BoatTable.Cols.NAME));
        int current = getInt(getColumnIndex(BoatTable.Cols.CURRENT));

        boolean isCox = (cox != 0);
        Boat boat = new Boat(UUID.fromString(uuid), BoatLab.toBoatSize(size), isCox, name);
        boat.setCurrent(current != 0);

        return boat;

    }
}
