package se.umu.saha5924.bettbok.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Calendar;
import java.util.UUID;

import se.umu.saha5924.bettbok.model.Bite;

import se.umu.saha5924.bettbok.database.BiteDbSchema.BiteTable;

/**
 * BiteCursorWrapper is responsible for retrieving a Bite from a Cursor.
 */
public class BiteCursorWrapper extends CursorWrapper {

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public BiteCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Retrieves a Bite from the CursorWrapper.
     *
     * @return The retrieved Bite.
     */
    public Bite getBite() {
        String uuidString = getString(getColumnIndex(BiteTable.Cols.UUID));
        String placement = getString(getColumnIndex(BiteTable.Cols.PLACEMENT));
        long calendar = getLong(getColumnIndex(BiteTable.Cols.CALENDAR));
        String stage = getString(getColumnIndex(BiteTable.Cols.STAGE));

        Bite bite = new Bite(UUID.fromString(uuidString));
        bite.setPlacement(placement);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(calendar);
        bite.setCalendar(c);
        bite.setStage(stage);

        return bite;
    }
}
