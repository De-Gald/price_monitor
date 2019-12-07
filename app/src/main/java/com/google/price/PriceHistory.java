package com.google.price;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.ml.md.R;
import com.google.price.data.PriceContract;
import com.google.price.data.PriceDbHelper;


public class PriceHistory extends AppCompatActivity implements PriceAdapter.PriceOnClickHandler {

    private PriceAdapter mAdapter;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.price_history);

        RecyclerView PriceRecyclerView;

        PriceRecyclerView = (RecyclerView) this.findViewById(R.id.corners_list_view);

        PriceRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        PriceDbHelper dbHelper = new PriceDbHelper(this);

        mDb = dbHelper.getWritableDatabase();

        Cursor cursor = getAllPriceData();

        mAdapter = new PriceAdapter(this, cursor, this);

        PriceRecyclerView.setAdapter(mAdapter);

        String jsonString = getIntent().getStringExtra("JSON");
        String link = "https://www.ebay.com/itm/Microsoft-Surface-Pro-7-12-3-Intel-Core-i5-8GB-RAM-128GB-SSD-Type-Cover/303341168365?_trkparms=5373%3A0%7C5374%3AFeatured%7C5079%3A6000001154";
        String value = jsonString;
        addNewRecord(value, link);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                removePriceRecord(id);
                mAdapter.swapCursor(getAllPriceData());
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
                PriceContract.PriceEntry.COLUMN_TIMESTAMP
        );
    }

    private long addNewRecord(String price, String link) {
        ContentValues cv = new ContentValues();
        cv.put(PriceContract.PriceEntry.COLUMN_VALUE, price);
        cv.put(PriceContract.PriceEntry.COLUMN_LINK, link);
        return mDb.insert(PriceContract.PriceEntry.TABLE_NAME, null, cv);
    }


    private boolean removePriceRecord(long id) {
        return mDb.delete(PriceContract.PriceEntry.TABLE_NAME, PriceContract.PriceEntry._ID + "=" + id, null) > 0;
    }

    @Override
    public void onClick(String link) {
        String url = link;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}