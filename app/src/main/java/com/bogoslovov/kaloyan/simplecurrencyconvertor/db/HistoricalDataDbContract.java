package com.bogoslovov.kaloyan.simplecurrencyconvertor.db;

import android.content.ContentResolver;
import android.content.ContentUris;
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

        public static Uri buildHistoricalDataUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildChartDataUri(String currency, String secondCurrency){
            return CONTENT_URI.buildUpon().appendPath(currency).appendPath(secondCurrency).build();
        }

        public static String[] getCurrenciesAndDatesForChart(Uri uri){
            String firstCompany = uri.getPathSegments().get(1);
            String secondCompany = uri.getPathSegments().get(2);
            String[] currenciesAndDates = {firstCompany,secondCompany,"DATE"};
            return currenciesAndDates;
        }

        public static final String TABLE_NAME = "historicaldata";
    }
}
