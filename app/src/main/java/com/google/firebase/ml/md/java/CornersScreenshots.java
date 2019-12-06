package com.google.firebase.ml.md.java;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.ml.md.R;
import com.google.firebase.ml.md.java.data.CornersContract;
import com.google.firebase.ml.md.java.data.CornersDbHelper;


public class CornersScreenshots extends AppCompatActivity implements CornersAdapter.CornersOnClickHandler {

    private CornersAdapter mAdapter;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.corners_screenshots);

        RecyclerView cornersRecyclerView;

        cornersRecyclerView = (RecyclerView) this.findViewById(R.id.corners_list_view);

        cornersRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        CornersDbHelper dbHelper = new CornersDbHelper(this);

        mDb = dbHelper.getWritableDatabase();

        Cursor cursor = getAllCornersData();

        mAdapter = new CornersAdapter(this, cursor, this);

        cornersRecyclerView.setAdapter(mAdapter);

        String jsonString = getIntent().getStringExtra("JSON");
        String path_to_Screenshot = "path to the File";
        String team = jsonString;
        addNewRecord(team, path_to_Screenshot);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                removeCornersRecord(id);
                mAdapter.swapCursor(getAllCornersData());
            }

        }).attachToRecyclerView(cornersRecyclerView);

    }


    private Cursor getAllCornersData() {
        return mDb.query(
                CornersContract.WaitlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                CornersContract.WaitlistEntry.COLUMN_TIMESTAMP
        );
    }

    private long addNewRecord(String team, String pathToFile) {
        ContentValues cv = new ContentValues();
        cv.put(CornersContract.WaitlistEntry.COLUMN_TEAM_NAME, team);
        cv.put(CornersContract.WaitlistEntry.COLUMN_PATH_TO_SCREENSHOT, pathToFile);
        return mDb.insert(CornersContract.WaitlistEntry.TABLE_NAME, null, cv);
    }


    private boolean removeCornersRecord(long id) {
        return mDb.delete(CornersContract.WaitlistEntry.TABLE_NAME, CornersContract.WaitlistEntry._ID + "=" + id, null) > 0;
    }

    @Override
    public void onClick(String pathToFile) {
        Toast.makeText(this, "Click event is triggered", Toast.LENGTH_LONG).show();
    }
}