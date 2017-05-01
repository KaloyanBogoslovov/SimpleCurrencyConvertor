package com.bogoslovov.kaloyan.simplecurrencyconvertor.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bogoslovov.kaloyan.simplecurrencyconvertor.R;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by kaloqn on 5/1/17.
 */

public class HistoricalDataDbOpenHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "historical_data.db";
    private Context context;

    public HistoricalDataDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.init_db);
            String initSql = IOUtils.toString(inputStream, Charset.forName("UTF-8"));
            db.execSQL(initSql);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS historicaldata");
        onCreate(db);
    }
}
