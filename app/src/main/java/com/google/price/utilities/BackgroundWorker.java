package com.google.price.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.price.data.PriceContract;
import com.google.price.data.PriceDbHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class BackgroundWorker extends Worker {
    Context context = null;
    SQLiteDatabase mDb;

    public BackgroundWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
        PriceDbHelper dbHelper = new PriceDbHelper(context);
        mDb = dbHelper.getWritableDatabase();
    }

    @Override
    public Result doWork() {

        Cursor cursor = getAllPriceData();

        int size = cursor.getCount();

        if (size != 0) {
            ArrayList<Float> oldPrices = new ArrayList<>();
            ArrayList<String> linksToPages = new ArrayList<>();
            ArrayList<Integer> ids = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                cursor.moveToPosition(i);
                ids.add(cursor.getInt(cursor.getColumnIndex(PriceContract.PriceEntry._ID)));
                oldPrices.add(Float.valueOf(cursor.getString(cursor.getColumnIndex(PriceContract.PriceEntry.COLUMN_VALUE))));
                linksToPages.add(cursor.getString(cursor.getColumnIndex(PriceContract.PriceEntry.COLUMN_LINK_TO_PAGE)));
            }

            //fetching new prices
            ArrayList<Float> newPrices = Updater.update(linksToPages);

            //check if prices have changed
            boolean flag = false;
            for (int i = 0; i < newPrices.size(); i++) {
                if (newPrices.get(i) < oldPrices.get(i)) {
                    updateValue(ids.get(i), newPrices.get(i));
                    flag = true;
                }
            }

            //if prices have changed, show notification
            if (flag)
                NotificationHandler.priceDropedNotification(context);
        }
        return Result.success();
    }

    private Cursor getAllPriceData() {
        return mDb.query(
                PriceContract.PriceEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                PriceContract.PriceEntry._ID

        );
    }

    private void updateValue(int position, Float newPrice) {
        //update time of an record with changed price
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
        String time = currentTimestamp.toString();

        ContentValues cv = new ContentValues();
        cv.put(PriceContract.PriceEntry.COLUMN_PRICE_UPDATED, 1);
        cv.put(PriceContract.PriceEntry.COLUMN_TIMESTAMP, time);
        cv.put(PriceContract.PriceEntry.COLUMN_VALUE, newPrice);

        mDb.update(PriceContract.PriceEntry.TABLE_NAME, cv, PriceContract.PriceEntry._ID + "=" + position, null);
    }
}
