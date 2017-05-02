package com.bogoslovov.kaloyan.simplecurrencyconvertor.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
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

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = HistoricalDataDbContract.CONTENT_AUTHORITY;
        matcher.addURI(authority,HistoricalDataDbContract.PATH_HISTORICAL_DATA, HISTORICAL_DATA);
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

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
