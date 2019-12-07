package com.google.price.data;

import android.provider.BaseColumns;

public class PriceContract {

    public static final class PriceEntry implements BaseColumns {
        public static final String TABLE_NAME = "priceData";
        public static final String COLUMN_VALUE = "item_price";
        public static final String COLUMN_LINK = "link";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

}
