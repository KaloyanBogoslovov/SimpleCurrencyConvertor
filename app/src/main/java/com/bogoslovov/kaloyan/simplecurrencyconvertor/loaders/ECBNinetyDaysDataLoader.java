package com.bogoslovov.kaloyan.simplecurrencyconvertor.loaders;

import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.bogoslovov.kaloyan.simplecurrencyconvertor.constants.Constants;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.db.HistoricalDataDbContract;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.dtos.DataFromServerDTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Kaloyan on 29/05/2017.
 */

public class ECBNinetyDaysDataLoader  extends AsyncTaskLoader<DataFromServerDTO> {
    private Context context;


    public ECBNinetyDaysDataLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public DataFromServerDTO loadInBackground() {

        DataFromServerDTO data=null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(Constants.ECB_90_DAYS_URL).build();
            Response response = client.newCall(request).execute();

            data = new DataFromServerDTO();
            data.setResponseCode(response.code());
            InputStream is = new ByteArrayInputStream(response.body().bytes());

            parseAndSaveDataInDB(is);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    private void parseAndSaveDataInDB(InputStream is){
        try{
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myParser = xmlFactoryObject.newPullParser();
            myParser.setInput(is,null);

            int event = myParser.getEventType();
            int i = 0;
            List<ContentValues> contentValuesList = new ArrayList<>();
            ContentValues contentValues = null;

            while (event != XmlPullParser.END_DOCUMENT)  {
                String name=myParser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        if(name.equals("Cube")) {
                            if (i==1){
                                contentValues = new ContentValues();
                                contentValues.put("EUR", "1");
                                contentValues.put("date",myParser.getAttributeValue(null, "time"));
                            }else if (i>1){
                                contentValues.put(myParser.getAttributeValue(null,"currency"), myParser.getAttributeValue(null,"rate"));
                            }
                            if (i==32){
                                contentValuesList.add(contentValues);
                                i=0;
                            }
                            i++;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        break;
                }
                event = myParser.next();
            }
            is.close();
            saveHistoricalDataToDB(contentValuesList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private void saveHistoricalDataToDB(List<ContentValues> contentValuesList){
        ContentValues[] contentValuesArray = contentValuesList.toArray(new ContentValues[contentValuesList.size()]);

        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.delete(HistoricalDataDbContract.HistoricalDataEntry.CONTENT_URI,null,null);
        contentResolver.bulkInsert(HistoricalDataDbContract.HistoricalDataEntry.CONTENT_URI,contentValuesArray);
        testDB(contentResolver);
    }

    private void testDB(ContentResolver contentResolver){
        Cursor cursor = contentResolver.query(HistoricalDataDbContract.HistoricalDataEntry.CONTENT_URI,null,null,null,null);

        while (cursor.moveToNext()){
            String date = cursor.getString(cursor.getColumnIndex("DATE"));
            String huf = cursor.getString(cursor.getColumnIndex("HUF"));
            String zar = cursor.getString(cursor.getColumnIndex("ZAR"));
            System.out.println("Date: "+date+" HUF: "+huf+" ZAR: "+zar);
        }
        cursor.close();
    }


}