package se.umu.saha5924.bettbok;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import se.umu.saha5924.bettbok.database.BiteBaseHelper;
import se.umu.saha5924.bettbok.database.BiteCursorWrapper;
import se.umu.saha5924.bettbok.database.BiteDbSchema.BiteTable;

/**
 * BiteLab is responsible for handling requests concerning the Bites in the database.
 */
public class BiteLab {

    private SQLiteDatabase mDatabase;
    private static BiteLab biteLab;
    private Context mContext;

    /**
     * Will return an existing BiteLab if there is one.
     * Otherwise a new BiteLab is returned.
     *
     * @param context The application context.
     * @return The BiteLab.
     */
    public static BiteLab get(Context context) {
        if (biteLab == null) biteLab = new BiteLab(context);
        return biteLab;
    }

    // Used for creating a new BiteLab.
    private BiteLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new BiteBaseHelper(mContext).getWritableDatabase();
    }

    /**
     * Add given Bite to database.
     *
     * @param bite The Bite to be added.
     */
    public void addBite(Bite bite) {
        mDatabase.insert(BiteTable.NAME, null, getContentValues(bite));
    }

    /**
     * Update given Bite in database.
     *
     * @param bite The Bite to be updated.
     */
    public void updateBite(Bite bite) {
        String uuidString = bite.getId().toString();
        ContentValues values = getContentValues(bite);

        // The UUID is used to find and update the row in the database
        // that contains the Bite with the corresponding UUID.
        mDatabase.update(BiteTable.NAME
                , values
                ,BiteTable.Cols.UUID + " = ?"
                , new String[] { uuidString });
    }

    /**
     * Delete the given Bite from the database.
     *
     * @param bite The Bite to be deleted.
     */
    public void deleteBite(Bite bite) {
        String uuidString = bite.getId().toString();
        mDatabase.delete(BiteTable.NAME
                , BiteTable.Cols.UUID + " = ?"
                , new String[] {uuidString});
    }

    /**
     * Get the Bite with the given id from the database.
     *
     * @param id The id of the Bite to be fetched.
     * @return The Bite with the given id.
     */
    public Bite getBite(UUID id) {
        try (BiteCursorWrapper cursor = queryBites(
                BiteTable.Cols.UUID + " = ?"
                , new String[]{id.toString()})) {
            if (cursor.getCount() == 0) return null;
            cursor.moveToFirst();
            return cursor.getBite();
        }
    }

    /**
     * Get a list of all Bites from the database.
     * The list is sorted by how old the Bites are, where the oldest Bite is last.
     *
     * @return All the Bites in the database.
     */
    public List<Bite> getBites() {
        List<Bite> bites = new ArrayList<>();

        try (BiteCursorWrapper cursor = queryBites(null, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                bites.add(cursor.getBite());
                cursor.moveToNext();
            }
        }
        sortBites(bites);
        return bites;
    }

    /**
     * Get the File for the first image connected to given bite.
     *
     * @param bite The Bite connected to the File.
     * @return The File of the first image.
     */
    public File getFirstImageFile(Bite bite) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, bite.getImageFilename(1));
    }

    /**
     * Get the File for the second image connected to given bite.
     *
     * @param bite The Bite connected to the File.
     * @return The File of the second image.
     */
    public File getSecondImageFile(Bite bite) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, bite.getImageFilename(2));
    }

    /**
     * Get the File for the third image connected to given bite.
     *
     * @param bite The Bite connected to the File.
     * @return The File of the third image.
     */
    public File getThirdImageFile(Bite bite) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, bite.getImageFilename(3));
    }

    // Sort a list of Bites by how old they are, with the oldest Bite last.
    private void sortBites(List<Bite> bites) {
        Collections.sort(bites, new SortByTime());
    }

    // A Comparator for sorting Bites by how old they are.
    private static class SortByTime implements Comparator<Bite> {
        @Override
        public int compare(Bite b1, Bite b2) {
             if (b1.getCalendar().getTimeInMillis() > b2.getCalendar().getTimeInMillis()) {
                 return -1;
             } else if (b1.getCalendar().getTimeInMillis() == b2.getCalendar().getTimeInMillis()) {
                 return 0;
             }
             return 1;
        }
    }

    // Make a Bite into a ContentValues to be stored in SQLite.
    private static ContentValues getContentValues(Bite bite) {
        ContentValues values = new ContentValues();
        values.put(BiteTable.Cols.UUID, bite.getId().toString());
        values.put(BiteTable.Cols.PLACEMENT, bite.getPlacement());
        values.put(BiteTable.Cols.CALENDAR, bite.getCalendar().getTimeInMillis());
        values.put(BiteTable.Cols.STAGE, bite.getStage());

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
