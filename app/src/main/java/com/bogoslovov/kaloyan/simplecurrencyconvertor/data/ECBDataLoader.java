package com.bogoslovov.kaloyan.simplecurrencyconvertor.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;

import com.bogoslovov.kaloyan.simplecurrencyconvertor.dto.DataFromServerDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.attr.action;
import static com.bogoslovov.kaloyan.simplecurrencyconvertor.Constants.ECB_URL;

/**
 * Created by Kaloyan on 15.11.2016 г..
 */

public class ECBDataLoader extends AsyncTaskLoader<DataFromServerDTO> {
    private static final int CONNECTION_TIMEOUT= Integer.parseInt(System.getProperty("ECB.connection.timeout", "10000"));
    public static SharedPreferences sharedPreferences;

  public ECBDataLoader(Context context) {
      super(context);
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
          Response response = client.newCall(ECB_URL).execute();
          String body = response.body().string();
          int responseCode = response.code();
          data = new DataFromServerDTO();
          data.setBody(body);
          data.setResponseCode(responseCode);
      } catch (IOException e) {
          e.printStackTrace();
      }

      return data;

        updateDataFromECB();
        return null;
  }

    private void updateDataFromECB() {
        URLConnection connection =setConnectionWithECB(ECB_URL);
        getInformationFromECB(connection);
    }

    private URLConnection setConnectionWithECB(String url) {
        URLConnection connection = null;
        try {
            URL request = new URL(url);
            connection = request.openConnection();
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(CONNECTION_TIMEOUT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void getInformationFromECB(URLConnection connection) {
        try {
            InputStreamReader is = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(is);
            for (int i = 0; i < 7; i++) {
                br.readLine();
            }
            parseData(br);
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("getInformationFromECB exception");
        }
    }

    private void parseData(BufferedReader br) throws IOException {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String line = br.readLine();
        String date = line.substring(14, 24);
        editor.putString("EUR", "1");
        editor.putString("date",date);
        String remaining;
        String symbol;
        String rate;
        for (int i = 0; i < 31; i++) {
            line = br.readLine();
            remaining = line.substring(19);
            symbol = remaining.substring(0,3);
            rate = remaining.substring(11, remaining.length()-3);
            editor.putString(symbol, rate);

        }
        System.out.println("data refreshed");
        editor.commit();
        br.close();

    }

}
