package se.umu.saha5924.bettbok;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import se.umu.saha5924.bettbok.database.BiteBaseHelper;
import se.umu.saha5924.bettbok.database.BiteCursorWrapper;
import se.umu.saha5924.bettbok.database.BiteDbSchema.BiteTable;

public class BiteLab {

    private SQLiteDatabase mDatabase;
    private static BiteLab biteLab;

    public static BiteLab get(Context context) {
        if (biteLab == null) biteLab = new BiteLab(context);
        return biteLab;
    }

    private BiteLab(Context context) {
        mDatabase = new BiteBaseHelper(context.getApplicationContext())
                .getWritableDatabase();



        /*
        mBett = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Bett bett = new Bett();
            bett.setmPlacering("Bett #" + i);
            mBett.add(bett);
        }*/
    }

    public void addBite(Bite b) {
        mDatabase.insert(BiteTable.NAME, null, getContentValues(b));
    }

    public void updateBite(Bite b) {
        String uuidString = b.getId().toString();
        ContentValues values = getContentValues(b);

        // The UUID is used to find and update the row in the database,
        // that contains the Bite with the corresponding UUID.
        mDatabase.update(BiteTable.NAME
                , values
                ,BiteTable.Cols.UUID + " = ?"
                , new String[] { uuidString });
    }

    public void deleteBite(Bite b) {
        String uuidString = b.getId().toString();
        mDatabase.delete(BiteTable.NAME
                , BiteTable.Cols.UUID + " = ?"
                , new String[] {uuidString});
    }

    public List<Bite> getBites() {
        List<Bite> bites = new ArrayList<>();
        BiteCursorWrapper cursor = queryBites(null, null); // TODO

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                bites.add(cursor.getBite());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        sortBites(bites);
        return bites;
    }

    public Bite getBite(UUID id) {
        BiteCursorWrapper cursor = queryBites(
                BiteTable.Cols.UUID + " = ?"
                , new String[] { id.toString() });

        try {
            if (cursor.getCount() == 0) return null;
            cursor.moveToFirst();
            return cursor.getBite();
        } finally {
            cursor.close();
        }
    }

    public boolean biteExist(UUID id) {
        BiteCursorWrapper cursor = queryBites(
                BiteTable.Cols.UUID + " = ?"
                , new String[] { id.toString() });

        try {
            return cursor.getCount() != 0;
        } finally {
            cursor.close();
        }
    }

    private void sortBites(List<Bite> bites) {
        Collections.sort(bites, new SortByTime());
    }

    private class SortByTime implements Comparator<Bite> {
        @Override
        public int compare(Bite b1, Bite b2) {
             if (b1.getCalendar().getTimeInMillis() > b2.getCalendar().getTimeInMillis())
                 return -1;
             return 1;
        }
    }

    // Make a Bite into a ContentValues to be stored in SQLite.
    private static ContentValues getContentValues(Bite bite) {
        ContentValues values = new ContentValues();
        values.put(BiteTable.Cols.UUID, bite.getId().toString());
        values.put(BiteTable.Cols.PLACEMENT, bite.getPlacement());
        values.put(BiteTable.Cols.CALENDAR, bite.getCalendar().getTimeInMillis());

        return values;
    }

    // Returns a Cursor with the query containing the Bites.
    private BiteCursorWrapper queryBites(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                BiteTable.NAME
                ,null
                , whereClause
                , whereArgs
                ,null
                ,null
                ,null
        );
        return new BiteCursorWrapper(cursor);
    }
}
