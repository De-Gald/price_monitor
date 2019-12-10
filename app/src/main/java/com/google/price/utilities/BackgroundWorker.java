package com.google.price.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.price.data.PriceContract;
import com.google.price.data.PriceDbHelper;

import java.util.ArrayList;
import java.util.Arrays;

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
        ArrayList<Float> oldPrices = new ArrayList<>();
        ArrayList<String> linksToPages = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            cursor.moveToPosition(i);
            ids.add(cursor.getInt(cursor.getColumnIndex(PriceContract.PriceEntry._ID)));
            oldPrices.add(Float.valueOf(cursor.getString(cursor.getColumnIndex(PriceContract.PriceEntry.COLUMN_VALUE))));
            linksToPages.add(cursor.getString(cursor.getColumnIndex(PriceContract.PriceEntry.COLUMN_LINK_TO_PAGE)));
        }
        cursor.moveToFirst();
        oldPrices.set(0, (float) 1000);
        oldPrices.set(1, (float) 1000);
        ArrayList<Float> newPrices = Updater.update(linksToPages);

        ArrayList<Integer> itemsPriceReduced = new ArrayList<>();
        boolean flag = false;
        for (int i = 0; i < newPrices.size(); i++) {
            if (newPrices.get(i) < oldPrices.get(i)) {
                itemsPriceReduced.add(ids.get(i));
                flag = true;
            }
        }

        for (Integer id: itemsPriceReduced){
            updateValue(id);
        }
        if (flag)
            NotificationHandler.priceDropedNotification(context);
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
                PriceContract.PriceEntry.COLUMN_TIMESTAMP
        );
    }

    private void updateValue(int position) {
        ContentValues cv = new ContentValues();
        cv.put(PriceContract.PriceEntry.COLUMN_PRICE_UPDATED, 1);
        String whereClause = PriceContract.PriceEntry._ID + "=" + position;
        String[] args = new String[]{String.valueOf(2)};
        mDb.update(PriceContract.PriceEntry.TABLE_NAME, cv, whereClause, null);
    }
}
