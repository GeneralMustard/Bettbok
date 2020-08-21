package se.umu.saha5924.bettbok.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import se.umu.saha5924.bettbok.database.BiteDbSchema.BiteTable;

/**
 * BiteBaseHelper is responsible for creating the database used for storing the Bites.
 */
public class BiteBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "bitebase.db";

    public BiteBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + BiteTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                BiteTable.Cols.UUID + ", " +
                BiteTable.Cols.PLACEMENT + ", " +
                BiteTable.Cols.CALENDAR  + "," +
                BiteTable.Cols.STAGE + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not implemented.
    }
}
