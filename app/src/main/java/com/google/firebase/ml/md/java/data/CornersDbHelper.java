package com.google.firebase.ml.md.java.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class CornersDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "corners.db";

    //increase the number if change database schema
    private static final int DATABASE_VERSION = 1;

    public CornersDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold corners data
        final String SQL_CREATE_WAITLIST_TABLE = "CREATE TABLE " + CornersContract.WaitlistEntry.TABLE_NAME + " (" +
                CornersContract.WaitlistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CornersContract.WaitlistEntry.COLUMN_TEAM_NAME + " STRING NOT NULL, " +
                CornersContract.WaitlistEntry.COLUMN_PATH_TO_SCREENSHOT + " STRING NOT NULL, " +
                CornersContract.WaitlistEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_WAITLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CornersContract.WaitlistEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}