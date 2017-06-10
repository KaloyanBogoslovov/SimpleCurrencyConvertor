package com.bogoslovov.kaloyan.simplecurrencyconvertor.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;

import com.bogoslovov.kaloyan.simplecurrencyconvertor.constants.Constants;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.constants.URLConstants;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.dtos.DataFromServerDTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Kaloyan on 15.11.2016 г..
 */

public class ECBDailyDataLoader extends AsyncTaskLoader<DataFromServerDTO> {

    private SharedPreferences sharedPreferences;
    public ECBDailyDataLoader(Context context) {
        super(context);
    }

    public ECBDailyDataLoader(Context context,SharedPreferences sharedPreferences) {
        super(context);
        this.sharedPreferences = sharedPreferences;
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
            Request request = new Request.Builder().url(URLConstants.ECB_DAILY_URL).build();
            Response response = client.newCall(request).execute();

            data = new DataFromServerDTO();
            data.setResponseCode(response.code());
            InputStream is = new ByteArrayInputStream(response.body().bytes());

            parseAndSaveDataInSharedPreferences(is);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    private void parseAndSaveDataInSharedPreferences(InputStream is) {
        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myParser = xmlFactoryObject.newPullParser();
            myParser.setInput(is, null);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            int event = myParser.getEventType();
            int i = 0;

            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if (name.equals("Cube")) {
                            if (i == 1) {
                                editor.putString("EUR", "1");
                                editor.putString("date", myParser.getAttributeValue(null, "time"));
                            } else if (i > 1) {
                                editor.putString(myParser.getAttributeValue(null, "currency"), myParser.getAttributeValue(null, "rate"));
                            }
                            if (i == 32) i = 0;
                            i++;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        break;
                }
                event = myParser.next();
            }

            editor.commit();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
