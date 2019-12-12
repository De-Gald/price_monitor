package com.google.price.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class PriceDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "price.db";

    private static final int DATABASE_VERSION = 1;

    public PriceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_PRICE_TABLE = "CREATE TABLE " + PriceContract.PriceEntry.TABLE_NAME + " (" +
                PriceContract.PriceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PriceContract.PriceEntry.COLUMN_VALUE + " STRING NOT NULL, " +
                PriceContract.PriceEntry.COLUMN_TITLE + " STRING NOT NULL, " +
                PriceContract.PriceEntry.COLUMN_LINK_TO_PAGE + " STRING NOT NULL, " +
                PriceContract.PriceEntry.COLUMN_LINK_TO_ICON + " STRING NOT NULL, " +
                PriceContract.PriceEntry.COLUMN_PRICE_UPDATED + " INTEGER DEFAULT 0, " +
                PriceContract.PriceEntry.COLUMN_TIMESTAMP + " TIMESTAMP" +
                "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_PRICE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PriceContract.PriceEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}