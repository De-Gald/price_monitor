package com.google.price.utilities;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.price.data.PriceDbHelper;

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

//        Cursor cursor = getAllPriceData();

        NotificationHandler.priceDropedNotification(context);
        return Result.success();
    }
}
