package com.google.firebase.ml.md.java.data;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    public static void insertFakeData(SQLiteDatabase db) {
        if (db == null) {
            return;
        }
        List<ContentValues> list = new ArrayList<ContentValues>();

        ContentValues cv = new ContentValues();
        cv.put(CornersContract.WaitlistEntry.COLUMN_TEAM_NAME, "Barselona");
        cv.put(CornersContract.WaitlistEntry.COLUMN_PATH_TO_SCREENSHOT, "path to file");
        list.add(cv);

        cv = new ContentValues();
        cv.put(CornersContract.WaitlistEntry.COLUMN_TEAM_NAME, "Barselona2");
        cv.put(CornersContract.WaitlistEntry.COLUMN_PATH_TO_SCREENSHOT, "path to file2");
        list.add(cv);

        cv = new ContentValues();
        cv.put(CornersContract.WaitlistEntry.COLUMN_TEAM_NAME, "Barselona3");
        cv.put(CornersContract.WaitlistEntry.COLUMN_PATH_TO_SCREENSHOT, "path to file3");
        list.add(cv);

        cv = new ContentValues();
        cv.put(CornersContract.WaitlistEntry.COLUMN_TEAM_NAME, "Barselona4");
        cv.put(CornersContract.WaitlistEntry.COLUMN_PATH_TO_SCREENSHOT, "path to file4");
        list.add(cv);

        cv = new ContentValues();
        cv.put(CornersContract.WaitlistEntry.COLUMN_TEAM_NAME, "Barselona5");
        cv.put(CornersContract.WaitlistEntry.COLUMN_PATH_TO_SCREENSHOT, "path to file5");
        list.add(cv);

        try {
            db.beginTransaction();
            db.delete(CornersContract.WaitlistEntry.TABLE_NAME, null, null);
            for (ContentValues c : list) {
                db.insert(CornersContract.WaitlistEntry.TABLE_NAME, null, c);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }
}