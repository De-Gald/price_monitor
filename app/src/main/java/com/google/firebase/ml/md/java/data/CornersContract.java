package com.google.firebase.ml.md.java.data;

import android.provider.BaseColumns;

public class CornersContract {

    public static final class WaitlistEntry implements BaseColumns {
        public static final String TABLE_NAME = "cornersData";
        public static final String COLUMN_TEAM_NAME = "teamName";
        public static final String COLUMN_PATH_TO_SCREENSHOT = "pathToFile";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

}
