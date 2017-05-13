package com.bogoslovov.kaloyan.simplecurrencyconvertor.activities;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bogoslovov.kaloyan.simplecurrencyconvertor.Calculations;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.R;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.adapters.SpinnerAdapter;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.constants.Constants;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.db.HistoricalDataDbContract;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.dtos.DataFromServerDTO;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.loaders.ECBDataLoader;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.xmlparser.XMLParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.bogoslovov.kaloyan.simplecurrencyconvertor.constants.Constants.BOTTOM_SPINNER;
import static com.bogoslovov.kaloyan.simplecurrencyconvertor.constants.Constants.TOP_SPINNER;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<DataFromServerDTO> {

    private static final int ECB_DAILY_LOADER =1;
    private static final int ECB_90_DAYS_LOADER  = 2;
    private static String bottomSpinnerValue ="";
    private static String topSpinnerValue ="";
    private static int topSpinnerSelection=0;
    private static int bottomSpinnerSelection = 1;
    private SharedPreferences sharedPreferences;
    private EditText editTextTop;
    private EditText editTextBottom;
    private Spinner spinnerBottom;
    private Spinner spinnerTop;
    private XMLParser xmlParser;
    private Calculations calculations = new Calculations(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForConnection(ECB_DAILY_LOADER);
        checkIfSharedPreferenceExists();
        initSpinners();
        initSwapButton();
        initShowChartButton();
        initEditTextFields();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setElevation(0f);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("top-spinner-selection",topSpinnerSelection);
        outState.putInt("bottom-spinner-selection",bottomSpinnerSelection);
        outState.putString("top-edit-text-value", editTextTop.getText().toString());
        outState.putString("bottom-edit-text-value",editTextBottom.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        bottomSpinnerSelection = savedInstanceState.getInt("bottom-spinner-selection");
        topSpinnerSelection = savedInstanceState.getInt("top-spinner-selection");
        spinnerBottom.setSelection(bottomSpinnerSelection);
        spinnerTop.setSelection(topSpinnerSelection);
        editTextTop.setText(savedInstanceState.getString("top-edit-text-value"));
        editTextBottom.setText(savedInstanceState.getString("bottom-edit-text-value"));
    }

    private void checkForConnection(int loader){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() &&networkInfo.isAvailable()) {
            xmlParser = new XMLParser();
            startLoader(loader);
        }else{
            if (loader==ECB_DAILY_LOADER){
                setLastUpdateDate();
            }else if (loader ==ECB_90_DAYS_LOADER){
                //todo check if db is empty
                Intent intent = new Intent(MainActivity.this, ChartActivity.class);
                intent.putExtra(Constants.FIRST_CURRENCY,calculations.getSpinnerValue(topSpinnerValue));
                intent.putExtra(Constants.SECOND_CURRENCY,calculations.getSpinnerValue(bottomSpinnerValue));
                startActivity(intent);
            }
        }
    }

    private void startLoader(int loaderToStart){
        LoaderManager loaderManager = getLoaderManager();
        Loader<Object> passwordLoader = loaderManager.getLoader(loaderToStart);

        if(passwordLoader==null){
            loaderManager.initLoader(loaderToStart,null,this);
        }else{
            loaderManager.restartLoader(loaderToStart,null,this);
        }
    }

    private void setLastUpdateDate(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        TextView lastUpdate = (TextView) findViewById(R.id.last_update_text_view);
        String date = "Last update: "+sharedPref.getString("date","");
        lastUpdate.setText(date);
    }

    private void checkIfSharedPreferenceExists() {
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        if (!sharedPreferences.contains("EUR")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("EUR", "1");
            editor.putString("JPY", "116.95");
            editor.putString("BGN", "1.9558");
            editor.putString("CZK", "27.035");
            editor.putString("DKK", "7.4399");
            editor.putString("GBP", "0.86218");
            editor.putString("HUF", "309.49");
            editor.putString("USD", "1.0629");
            editor.putString("PLN", "4.4429");
            editor.putString("RON", "4.5150");
            editor.putString("SEK", "9.8243");
            editor.putString("CHF", "1.0711");
            editor.putString("NOK", "9.1038");
            editor.putString("HRK", "7.5320");
            editor.putString("RUB", "68.7941");
            editor.putString("TRY", "3.5798");
            editor.putString("AUD", "1.4376");
            editor.putString("BRL", "3.6049");
            editor.putString("CAD", "1.4365");
            editor.putString("CNY", "7.3156");
            editor.putString("HKD", "8.2450");
            editor.putString("IDR", "14272.09");
            editor.putString("ILS", "4.1163");
            editor.putString("INR", "72.2170");
            editor.putString("KRW", "1250.14");
            editor.putString("MXN", "21.6968");
            editor.putString("MYR", "4.6810");
            editor.putString("NZD", "1.5073");
            editor.putString("PHP", "52.687");
            editor.putString("SGD", "1.5107");
            editor.putString("THB", "37.744");
            editor.putString("ZAR", "15.2790");
            editor.putString("date","2016/11/18");
            editor.apply();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();

        if(id==R.id.action_about){
            Intent intent = new Intent (this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initSwapButton(){
        Button swapButton = (Button) findViewById(R.id.swap_button);
        swapButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Spinner spinnerBottom = (Spinner) findViewById(R.id.spinner_bottom);
                Spinner spinnerTop = (Spinner) findViewById(R.id.spinner_top);
                int position = spinnerBottom.getSelectedItemPosition();
                spinnerBottom.setSelection(spinnerTop.getSelectedItemPosition());
                spinnerTop.setSelection(position);

            }
        });
    }

    private void initShowChartButton(){
        Button showChartButton = (Button) findViewById(R.id.show_chart_button);
        showChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForConnection(ECB_90_DAYS_LOADER);

            }
        });
    }

    private void initEditTextFields() {
        editTextTop = (EditText) findViewById(R.id.edit_text_top);
        editTextBottom = (EditText) findViewById(R.id.edit_text_bottom);

        editTextTop.setText("1");
        editTextTop.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            public void afterTextChanged(Editable editable) {
                if (editTextTop.isFocused()) {
                    if (editTextTop.getText().toString().equals(".")){
                        editTextTop.setText("0.");
                        editTextTop.setSelection(editTextTop.getText().length());
                    }
                    else if(editTextTop.getText().toString().equals("")) {
                        editTextBottom.setText("0.000");
                    }else{
                        calculations.calculate(TOP_SPINNER, topSpinnerValue, bottomSpinnerValue);
                    }
                }
            }
        });


        editTextBottom.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            public void afterTextChanged(Editable editable) {
                if (editTextBottom.isFocused()) {
                    if(editTextBottom.getText().toString().equals("")) {
                        editTextTop.setText("0.000");
                    }else if (editTextBottom.getText().toString().equals(".")){
                        editTextBottom.setText("0.");
                        editTextBottom.setSelection(editTextBottom.getText().length());
                    }else{
                        calculations.calculate(BOTTOM_SPINNER, topSpinnerValue, bottomSpinnerValue);
                    }
                }
            }
        });
    }

    private void initSpinners(){
        Constants constants = new Constants();
        final SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_row,constants.currencies,constants.images);
        spinnerBottom = (Spinner) findViewById(R.id.spinner_bottom);
        spinnerTop = (Spinner) findViewById(R.id.spinner_top);
        spinnerTop.setSelection(topSpinnerSelection);
        spinnerTop.setAdapter(spinnerAdapter);
        spinnerBottom.setAdapter(spinnerAdapter);
        spinnerBottom.setSelection(bottomSpinnerSelection);
        spinnerTop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                topSpinnerValue = spinnerAdapter.getItem(position);
                topSpinnerSelection = position;
                calculations.calculate(TOP_SPINNER, topSpinnerValue, bottomSpinnerValue);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        spinnerBottom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                bottomSpinnerValue = spinnerAdapter.getItem(position);
                bottomSpinnerSelection = position;
                calculations.calculate(TOP_SPINNER, topSpinnerValue, bottomSpinnerValue);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    @Override
    public Loader<DataFromServerDTO> onCreateLoader(int loaderId, Bundle args) {

        ECBDataLoader loader = null;
        switch (loaderId){
            case ECB_DAILY_LOADER:
                Toast.makeText(this, "daily", Toast.LENGTH_SHORT).show();
                loader = new ECBDataLoader(this,Constants.ECB_DAILY_URL);
                break;

            case ECB_90_DAYS_LOADER:
                Toast.makeText(this, "90 days loader", Toast.LENGTH_SHORT).show();
                loader = new ECBDataLoader(this,Constants.ECB_90_DAYS_URL);
                break;
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<DataFromServerDTO> loader, DataFromServerDTO data) {

        switch (loader.getId()){
            case ECB_DAILY_LOADER:
                onLoadFinishedECBDailyLoader(data);
                break;
            case ECB_90_DAYS_LOADER:
                onLoadFinishedECB90DaysLoader(data);
                break;
        }

    }

    private void onLoadFinishedECBDailyLoader(DataFromServerDTO data){
        if(data.getResponseCode()==200) {
            try {
                BufferedReader br = data.getBody();
              for (int i = 0; i < 7; i++) {
                  br.readLine();
              }
              xmlParser.parseAndSaveDailyData(br,sharedPreferences);
            } catch (IOException e) {
                e.printStackTrace();
            }
            calculations.calculate(TOP_SPINNER, topSpinnerValue, bottomSpinnerValue);
            setLastUpdateDate();
            Toast.makeText(this, R.string.exchange_rates_updated, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, R.string.exchange_rates_update_failed, Toast.LENGTH_SHORT).show();
        }
    }



    private void onLoadFinishedECB90DaysLoader(DataFromServerDTO data){
        if(data.getResponseCode()==200) {
            try {
                BufferedReader br = data.getBody();
                String line;
                List<ContentValues> contentValuesList = new ArrayList<>();
                StringBuilder stringBuilder = new StringBuilder(br.readLine());

                //parse first row, because it's unique
                List<String> firstRow = xmlParser.parse90Days(stringBuilder.delete(0,299));

                //add first row
                contentValuesList.add(toContentValues(firstRow));

                //prepare stringBuilder for next row
                stringBuilder.setLength(0);

                // parse and add to the list all remaining rows
                while((line = br.readLine()) != null){
                    stringBuilder.append(line);
                    List<String> dataElement = xmlParser.parse90Days(stringBuilder);
                    contentValuesList.add(toContentValues(dataElement));
                    stringBuilder.setLength(0);
                }

                if (contentValuesList.size()>5){
                    saveHistoricalDataToDB(contentValuesList);
                }

                Intent intent = new Intent(MainActivity.this, ChartActivity.class);
                intent.putExtra(Constants.FIRST_CURRENCY,calculations.getSpinnerValue(topSpinnerValue));
                intent.putExtra(Constants.SECOND_CURRENCY,calculations.getSpinnerValue(bottomSpinnerValue));
                startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            calculations.calculate(TOP_SPINNER, topSpinnerValue, bottomSpinnerValue);
            setLastUpdateDate();
            Toast.makeText(this, R.string.exchange_rates_updated, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, R.string.exchange_rates_update_failed, Toast.LENGTH_SHORT).show();
        }
    }



    private void saveHistoricalDataToDB(List<ContentValues> contentValuesList){
        ContentValues[] contentValuesArray = contentValuesList.toArray(new ContentValues[contentValuesList.size()]);

        ContentResolver contentResolver = getContentResolver();
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

    private ContentValues toContentValues(List<String> list){
        ContentValues contentValues = new ContentValues();

        String[] keys = {"DATE","EUR","USD","JPY","BGN","CZK","DKK","GBP","HUF","PLN","RON",
            "SEK","CHF","NOK","HRK","RUB","TRY","AUD","BRL","CAD","CNY","HKD",
            "IDR","ILS","INR","KRW","MXN","MYR","NZD","PHP","SGD","THB","ZAR"};

        for (int i=0;i<keys.length;i++){
            contentValues.put(keys[i],list.get(i));
        }

        return contentValues;
    }

    @Override
    public void onLoaderReset(Loader<DataFromServerDTO> loader) {
    }
}
