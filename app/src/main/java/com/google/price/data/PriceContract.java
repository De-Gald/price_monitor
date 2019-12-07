package com.google.price.data;

import android.provider.BaseColumns;

public class PriceContract {

    public static final class PriceEntry implements BaseColumns {
        public static final String TABLE_NAME = "priceData";
        public static final String COLUMN_VALUE = "item_price";
        public static final String COLUMN_TITLE = "item_title";
        public static final String COLUMN_LINK_TO_PAGE = "link_to_page";
        public static final String COLUMN_LINK_TO_ICON = "link_to_icon";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

}
