package com.bogoslovov.kaloyan.simplecurrencyconvertor.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by kaloqn on 5/1/17.
 */

public class HistoricalDataDbProvider extends ContentProvider{

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private HistoricalDataDbOpenHelper db;

    private static final int HISTORICAL_DATA = 100;
    private static final int CHART_DATA = 101;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = HistoricalDataDbContract.CONTENT_AUTHORITY;
        matcher.addURI(authority,HistoricalDataDbContract.PATH_HISTORICAL_DATA, HISTORICAL_DATA);
        matcher.addURI(authority,HistoricalDataDbContract.PATH_HISTORICAL_DATA+"/*"+"/*", CHART_DATA);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        db = new HistoricalDataDbOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (uriMatcher.match(uri)){
            case HISTORICAL_DATA:
                cursor = db.getReadableDatabase().query(
                    HistoricalDataDbContract.HistoricalDataEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                );
                break;

            case CHART_DATA:
                String[] currenciesAndDatesColumns = HistoricalDataDbContract.HistoricalDataEntry.getCurrenciesAndDatesForChart(uri);
                cursor = db.getReadableDatabase().query(
                    HistoricalDataDbContract.HistoricalDataEntry.TABLE_NAME,
                    currenciesAndDatesColumns,
                    null,
                    null,
                    null,
                    null,
                    null
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case HISTORICAL_DATA:
                return HistoricalDataDbContract.HistoricalDataEntry.CONTENT_TYPE;
            case CHART_DATA:
                return HistoricalDataDbContract.HistoricalDataEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case HISTORICAL_DATA: {
                long _id = db.getWritableDatabase().insert(HistoricalDataDbContract.HistoricalDataEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = HistoricalDataDbContract.HistoricalDataEntry.buildHistoricalDataUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        int rowsDeleted;
        if (null == selection) selection = "1";
        switch (match) {
            case HISTORICAL_DATA:
                rowsDeleted = db.getWritableDatabase().delete(
                    HistoricalDataDbContract.HistoricalDataEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase database = db.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case HISTORICAL_DATA:
                database.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = database.insert(HistoricalDataDbContract.HistoricalDataEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
