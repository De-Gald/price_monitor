package com.google.price;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.ml.md.R;
import com.google.price.data.PriceContract;
import com.google.price.data.PriceDbHelper;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


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


        String jsonString = getIntent().getStringExtra("JSON");
        Map<String, String> itemInfo = null;
        try {
            itemInfo = PriceJsonUtils.getPriceStringsFromJson(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonString != null) {
            String value = itemInfo.get(PriceJsonUtils.PRICE);
            String title = itemInfo.get(PriceJsonUtils.TITLE);
            String link_to_page = itemInfo.get(PriceJsonUtils.LINK_TO_PAGE);
            String link_to_icon = itemInfo.get(PriceJsonUtils.LINK_TO_ICON);
            addNewRecord(value, title, link_to_page, link_to_icon);
        }

//        String value = "105";
//        String title = "Vintage Long Beach Ice Dogs Minor League ECHL Hockey Bomber Jacket Size Smal";
//        String link_to_page = "https://www.ebay.com/itm/Vintage-Long-Beach-Ice-Dogs-Minor-League-ECHL-Hockey-Bomber-Jacket-Size-Small/153696272533?_trkparms=aid%3D333200%26algo%3DCOMP.MBE%26ao%3D1%26asc%3D20171012094517%26meid%3Dcd0f4d5385304b25b75c714a0a40d734%26pid%3D100008%26rk%3D3%26rkt%3D12%26sd%3D303334029243%26itm%3D153696272533%26pmt%3D1%26noa%3D0%26pg%3D2047675&_trksid=p2047675.c100008.m2219";
//        String link_to_icon = "https://i.ebayimg.com/images/g/4JQAAOSwOYZdsUWJ/s-l500.jpg";
//        addNewRecord(value, title, link_to_page, link_to_icon);

        int size = cursor.getCount();
        ArrayList<Float> oldPrices = new ArrayList<>();
        ArrayList<String> linksToPages = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            cursor.moveToPosition(i);
            oldPrices.add(Float.valueOf(cursor.getString(cursor.getColumnIndex(PriceContract.PriceEntry.COLUMN_VALUE))));
            linksToPages.add(cursor.getString(cursor.getColumnIndex(PriceContract.PriceEntry.COLUMN_LINK_TO_PAGE)));
        }
        cursor.moveToFirst();

        int[] itemsPriceReduced = new int[cursor.getCount()];
        Arrays.fill(itemsPriceReduced, 0);
        itemsPriceReduced[0] = 1;

        mAdapter = new PriceAdapter(this, cursor, this, itemsPriceReduced);

        PriceRecyclerView.setAdapter(mAdapter);

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
    public void onClick(String link) {
        String url = link;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}