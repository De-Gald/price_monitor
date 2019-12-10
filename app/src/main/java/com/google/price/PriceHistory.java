package com.google.price;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.ml.md.R;
import com.google.price.data.PriceContract;
import com.google.price.data.PriceDbHelper;
import com.google.price.utilities.BackgroundWorker;
import com.google.price.utilities.PriceJsonUtils;

import org.json.JSONException;

import java.util.Map;
import java.util.concurrent.TimeUnit;


public class PriceHistory extends AppCompatActivity implements PriceAdapter.PriceOnClickHandler {

    private PriceAdapter mAdapter;
    private SQLiteDatabase mDb;
    RecyclerView PriceRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.price_history);

        PriceRecyclerView = (RecyclerView) this.findViewById(R.id.corners_list_view);
        PriceRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initialize connection to database
        PriceDbHelper dbHelper = new PriceDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        //add monitored item to the database
        String jsonString = getIntent().getStringExtra("JSON");
        if (jsonString != null) {
            Map<String, String> itemInfo = null;
            try {
                itemInfo = PriceJsonUtils.getPriceStringsFromJson(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (itemInfo != null) {
                String value = itemInfo.get(PriceJsonUtils.PRICE);
                String title = itemInfo.get(PriceJsonUtils.TITLE);
                String link_to_page = itemInfo.get(PriceJsonUtils.LINK_TO_PAGE);
                String link_to_icon = itemInfo.get(PriceJsonUtils.LINK_TO_ICON);
                addNewRecord(value, title, link_to_page, link_to_icon);
            }
        }

        //display database in RecyclerView
        Cursor cursor = getAllPriceData();
        mAdapter = new PriceAdapter(this, cursor, this);
        PriceRecyclerView.setAdapter(mAdapter);

        //schedule notifications in background
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getBoolean("NOTIFICATION_STARTER", false)) {
            PeriodicWorkRequest saveRequest =
                    new PeriodicWorkRequest.Builder(BackgroundWorker.class, 15, TimeUnit.MINUTES)
                            .setInitialDelay(60, TimeUnit.SECONDS)
                            .build();
            WorkManager.getInstance(this)
                    .enqueue(saveRequest);

            sharedPreferences.edit()
                    .putBoolean("NOTIFICATION_STARTER", true)
                    .apply();
        }


        //add swipe actions to RecyclerView
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                removePriceRecord(id);
                mAdapter = new PriceAdapter(PriceHistory.this, getAllPriceData(), PriceHistory.this);

                PriceRecyclerView.setAdapter(mAdapter);
            }

        }).attachToRecyclerView(PriceRecyclerView);

    }

    private Cursor getAllPriceData() {
        return mDb.query(
                PriceContract.PriceEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private long addNewRecord(String price, String title, String link_to_page, String link_to_icon) {
        ContentValues cv = new ContentValues();
        cv.put(PriceContract.PriceEntry.COLUMN_VALUE, price);
        cv.put(PriceContract.PriceEntry.COLUMN_TITLE, title);
        cv.put(PriceContract.PriceEntry.COLUMN_LINK_TO_PAGE, link_to_page);
        cv.put(PriceContract.PriceEntry.COLUMN_LINK_TO_ICON, link_to_icon);
        return mDb.insert(PriceContract.PriceEntry.TABLE_NAME, null, cv);
    }

    private boolean removePriceRecord(long id) {
        return mDb.delete(PriceContract.PriceEntry.TABLE_NAME, PriceContract.PriceEntry._ID + "=" + id, null) > 0;
    }

    @Override
    public void onClick(String link, int position) {
        //change the color of clicked record
        ContentValues cv = new ContentValues();
        cv.put(PriceContract.PriceEntry.COLUMN_PRICE_UPDATED, 0);

        Cursor cursor = getAllPriceData();
        cursor.moveToPosition(position);

        int id = cursor.getInt(cursor.getColumnIndex(PriceContract.PriceEntry._ID));
        mDb.update(PriceContract.PriceEntry.TABLE_NAME, cv, PriceContract.PriceEntry._ID + "=" + id, null);

        //renew data in RecyclerView
        mAdapter = new PriceAdapter(PriceHistory.this, getAllPriceData(), PriceHistory.this);
        PriceRecyclerView.setAdapter(mAdapter);

        //open item's link in a browser
        String url = link;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

}