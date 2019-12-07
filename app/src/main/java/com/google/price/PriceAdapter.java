package com.google.price;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.ml.md.R;
import com.google.price.data.PriceContract;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;


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

        new ImageLoaderClass().execute("https://i.ebayimg.com/images/g/wqYAAOSw4SJduRuU/s-l500.jpg");

//        while (imageFinal == null);

        String team = mCursor.getString(mCursor.getColumnIndex(PriceContract.PriceEntry.COLUMN_VALUE));
        String date = mCursor.getString(mCursor.getColumnIndex(PriceContract.PriceEntry.COLUMN_TIMESTAMP)).split("-")[1] + "-" + mCursor.getString(mCursor.getColumnIndex(PriceContract.PriceEntry.COLUMN_TIMESTAMP)).split("-")[2];
        long id = mCursor.getLong(mCursor.getColumnIndex(PriceContract.PriceEntry._ID));

        holder.itemInfo.setText(team + " - " + date);
        holder.itemLogo.setImageBitmap(imageFinal);
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
            String link = mCursor.getString(2);
            mPriceOnClickHandler.onClick(link);
        }
    }

    Bitmap imageFinal;
    Bitmap mImageBitmap;

    private class ImageLoaderClass extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected Bitmap doInBackground(String... args) {
            try {
                mImageBitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return mImageBitmap;
        }



        protected void onPostExecute(Bitmap image) {

            imageFinal = image;
        }
    }
}