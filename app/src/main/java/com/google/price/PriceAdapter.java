package com.google.price;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.ml.md.R;
import com.google.price.data.PriceContract;


public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.PriceViewHolder> {

    public interface PriceOnClickHandler {
        void onClick(String pathToFile);
    }

    private PriceOnClickHandler mPriceOnClickHandler;

    private Cursor mCursor;
    private Context mContext;

    public PriceAdapter(Context context, Cursor cursor, PriceOnClickHandler PriceOnClickHandler) {
        mPriceOnClickHandler = PriceOnClickHandler;
        this.mContext = context;
        this.mCursor = cursor;
    }

    @Override
    public PriceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.price_list_item, parent, false);
        return new PriceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PriceViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position))
            return;

        String team = mCursor.getString(mCursor.getColumnIndex(PriceContract.PriceEntry.COLUMN_VALUE));
        String date = mCursor.getString(mCursor.getColumnIndex(PriceContract.PriceEntry.COLUMN_TIMESTAMP)).split(" ")[0];
        long id = mCursor.getLong(mCursor.getColumnIndex(PriceContract.PriceEntry._ID));

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

    class PriceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView teamName;

        public PriceViewHolder(View itemView) {
            super(itemView);
            teamName = (TextView) itemView.findViewById(R.id.team_data_text_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mCursor.moveToPosition(getAdapterPosition());
            String link = mCursor.getString(2);
            mPriceOnClickHandler.onClick(link);
        }
    }
}