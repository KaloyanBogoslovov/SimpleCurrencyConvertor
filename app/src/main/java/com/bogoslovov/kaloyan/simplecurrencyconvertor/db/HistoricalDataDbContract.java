package com.bogoslovov.kaloyan.simplecurrencyconvertor.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by kaloqn on 5/1/17.
 */

public class HistoricalDataDbContract {
    public static final String CONTENT_AUTHORITY = "com.bogoslovov.kaloyan.simplecurrencyconvertor";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_HISTORICAL_DATA = "historicaldata";

    public static final class HistoricalDataEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_HISTORICAL_DATA).build();


        public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HISTORICAL_DATA;

        public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HISTORICAL_DATA;


        public static final String TABLE_NAME = "historicaldata";
    }
}
