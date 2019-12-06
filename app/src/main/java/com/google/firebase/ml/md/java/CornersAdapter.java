package com.google.firebase.ml.md.java;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.ml.md.R;
import com.google.firebase.ml.md.java.data.CornersContract;


public class CornersAdapter extends RecyclerView.Adapter<CornersAdapter.CornersViewHolder> {

    public interface CornersOnClickHandler {
        void onClick(String pathToFile);
    }

    private CornersOnClickHandler mCornersOnClickHandler;

    private Cursor mCursor;
    private Context mContext;

    public CornersAdapter(Context context, Cursor cursor, CornersOnClickHandler cornersOnClickHandler) {
        mCornersOnClickHandler = cornersOnClickHandler;
        this.mContext = context;
        this.mCursor = cursor;
    }

    @Override
    public CornersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.corners_list_item, parent, false);
        return new CornersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CornersViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position))
            return;

        String team = mCursor.getString(mCursor.getColumnIndex(CornersContract.WaitlistEntry.COLUMN_TEAM_NAME));
        String date = mCursor.getString(mCursor.getColumnIndex(CornersContract.WaitlistEntry.COLUMN_TIMESTAMP)).split(" ")[0];
        long id = mCursor.getLong(mCursor.getColumnIndex(CornersContract.WaitlistEntry._ID));

        holder.teamName.setText(team + " - " + date);
        holder.itemView.setTag(id);
    }


    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
    }

    class CornersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView teamName;

        public CornersViewHolder(View itemView) {
            super(itemView);
            teamName = (TextView) itemView.findViewById(R.id.team_data_text_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mCursor.moveToPosition(getAdapterPosition());
            String pathToFile = mCursor.getString(2);
            mCornersOnClickHandler.onClick(pathToFile);
        }
    }
}