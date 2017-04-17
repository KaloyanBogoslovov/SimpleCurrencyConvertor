package com.bogoslovov.kaloyan.simplecurrencyconvertor.data;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.TextView;

import com.bogoslovov.kaloyan.simplecurrencyconvertor.Calculations;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.MainActivity;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Kaloyan on 15.11.2016 г..
 */

public class ECBData extends AsyncTask<Object, Object, String> {
    public static final int CONNECTION_TIMEOUT= Integer.parseInt(System.getProperty("ECB.connection.timeout", "10000"));
    public static SharedPreferences sharedPreferences;
    private Activity activity;
    private String date="";
    Calculations calculations ;
    public ECBData(Activity activity){
        this.activity=activity;

    }

    protected String doInBackground(Object... voids) {
        getData();
        return date;
    }

    protected void onPostExecute(String date) {
        calculations = new Calculations(activity);
        calculations.calculate("top", MainActivity.topSpinnerValue,MainActivity.bottomSpinnerValue);
        TextView lastUpdate = (TextView) activity.findViewById(R.id.last_update_text_view);
        lastUpdate.setText("Last update: "+ date);
    }

    public void getData() {
        //String url ="http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
        String url = "http://192.168.1.102:8090/uni/v1/department/departments/";
        URLConnection connection =setConnectionWithECB(url);
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

            //for (int i = 0; i < 7; i++) {
                //br.readLine();
            System.out.println(br.readLine());
            //}
            //parseData(br);
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("getInformationFromECB exception");
        }
    }

    public void parseData(BufferedReader br) throws IOException {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String line = br.readLine();
        date = line.substring(14, 24);
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
