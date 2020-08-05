package se.umu.saha5924.bettbok;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import se.umu.saha5924.bettbok.database.BiteBaseHelper;
import se.umu.saha5924.bettbok.database.BiteCursorWrapper;
import se.umu.saha5924.bettbok.database.BiteDbSchema.BiteTable;

public class BettLab {

    //private static List<Bett> mBett;
    private SQLiteDatabase mDatabase;

    private static BettLab bettLab;

    public static BettLab get(Context context) {
        if (bettLab == null) {
            bettLab = new BettLab(context);
        }
        return bettLab;
    }
    private BettLab(Context context) {
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

    public void addBite(Bett b) {
        //mBett.add(b);
        mDatabase.insert(BiteTable.NAME, null, getContentValues(b));
    }

    public void updateBite(Bett b) {
        String uuidString = b.getmId().toString();
        ContentValues values = getContentValues(b);

        // The UUID is used to find and update the row in the database,
        // that contains the Bite with the corresponding UUID.
        mDatabase.update(BiteTable.NAME, values,
                BiteTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    public List<Bett> getBites() {
        List<Bett> bites = new ArrayList<>();
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
        return bites;
    }

    public Bett getBett(UUID id) {
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

    // Make a Bite into a ContentValues to be stored in SQLite.
    private static ContentValues getContentValues(Bett bite) {
        ContentValues values = new ContentValues();
        values.put(BiteTable.Cols.UUID, bite.getmId().toString());
        values.put(BiteTable.Cols.PLACEMENT, bite.getmPlacering());
        //values.put(BiteTable.Cols.CALENDAR, bite.getmDatum().toString()); //TODO
        values.put(BiteTable.Cols.CALENDAR, bite.getmDatum().getTimeInMillis());

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
