package com.google.price;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.ml.md.R;
import com.google.price.data.PriceContract;


public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.PriceViewHolder> {

    public interface PriceOnClickHandler {
        void onClick(String pathToFile, int position);
    }

    private PriceOnClickHandler mPriceOnClickHandler;

    private Cursor mCursor;
    private Context mContext;

    public PriceAdapter(Context context, Cursor cursor, PriceOnClickHandler PriceOnClickHandler) {
        mPriceOnClickHandler = PriceOnClickHandler;
        mContext = context;
        mCursor = cursor;
    }

    @Override
    public PriceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.price_list_item, parent, false);
        return new PriceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PriceViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) return;

        String title = mCursor.getString(mCursor.getColumnIndex(PriceContract.PriceEntry.COLUMN_TITLE));
        String link_to_icon = mCursor.getString(mCursor.getColumnIndex(PriceContract.PriceEntry.COLUMN_LINK_TO_ICON));
        String date = mCursor.getString(mCursor.getColumnIndex(PriceContract.PriceEntry.COLUMN_TIMESTAMP)).split("-")[1] + "-" + mCursor.getString(mCursor.getColumnIndex(PriceContract.PriceEntry.COLUMN_TIMESTAMP)).split("-")[2];
        long id = mCursor.getLong(mCursor.getColumnIndex(PriceContract.PriceEntry._ID));
        int priceUpdated = mCursor.getInt(mCursor.getColumnIndex(PriceContract.PriceEntry.COLUMN_PRICE_UPDATED));

        //change the color of an item if price has changed
        if (priceUpdated == 1)
            holder.itemInfo.setTextColor(Color.GREEN);

        Glide.with(mContext).load(link_to_icon).into(holder.itemLogo);
        holder.itemInfo.setText(title + " - " + date);
        holder.itemView.setTag(id);
    }


    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }


    class PriceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView itemInfo;
        ImageView itemLogo;

        public PriceViewHolder(View itemView) {
            super(itemView);
            itemInfo = (TextView) itemView.findViewById(R.id.item_text_view);
            itemLogo = (ImageView) itemView.findViewById(R.id.logo_image_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mCursor.moveToPosition(getAdapterPosition());
            String link = mCursor.getString(mCursor.getColumnIndex(PriceContract.PriceEntry.COLUMN_LINK_TO_PAGE));
            mPriceOnClickHandler.onClick(link, getAdapterPosition());
        }
    }

}