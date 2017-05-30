package com.bogoslovov.kaloyan.simplecurrencyconvertor.activities;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.bogoslovov.kaloyan.simplecurrencyconvertor.Utils;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.adapters.SpinnerAdapter;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.constants.Constants;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.db.HistoricalDataDbContract;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.dtos.DataFromServerDTO;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.fragments.LoadingFragment;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.loaders.ECBDailyDataLoader;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.loaders.ECBNinetyDaysDataLoader;

import static com.bogoslovov.kaloyan.simplecurrencyconvertor.constants.Constants.BOTTOM_SPINNER;
import static com.bogoslovov.kaloyan.simplecurrencyconvertor.constants.Constants.TOP_SPINNER;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<DataFromServerDTO> {

    private static final int ECB_DAILY_LOADER =1;
    private static final int ECB_90_DAYS_LOADER  = 2;
    private static String bottomSpinnerValue ="";
    private static String topSpinnerValue ="";
    private static int topSpinnerSelection=0;
    private static int bottomSpinnerSelection = 1;
    private static boolean onlineMode;
    private SharedPreferences sharedPreferences;
    private LoadingFragment loadingFragment;
    private Calculations calculations = new Calculations(this);
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkIfSharedPreferenceExists();
        onlineMode = getDataMode();
        checkForConnection(ECB_DAILY_LOADER);
        if (getIntent() != null && getIntent().getExtras()!=null){
            Intent intent = getIntent();
            String firstCurrency = intent.getStringExtra(Constants.FIRST_CURRENCY);
            String secondCurrency = intent.getStringExtra(Constants.SECOND_CURRENCY);
            topSpinnerSelection = Utils.getObjectNumber(Constants.currencyTags,firstCurrency);
            bottomSpinnerSelection = Utils.getObjectNumber(Constants.currencyTags, secondCurrency);
        }
        initSpinners();
        initSwapButton();
        initShowChartButton();
        initEditTextFields();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setElevation(0f);
        }

    }

    private boolean getDataMode(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean online = prefs.getBoolean("online-mode",true);

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                System.out.println("sharedPreference changed");
                if ("online-mode".equals(key)){
                    if( prefs.getBoolean("online-mode",true)){
                        onlineMode = true;
                    }else{
                        onlineMode = false;
                    }
                }
            }
        };

        prefs.registerOnSharedPreferenceChangeListener(listener);
        return online;
    }

    private void checkForConnection(int loader){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() &&networkInfo.isAvailable()&& onlineMode) {
            startLoader(loader);
        }else{
            if (loader==ECB_DAILY_LOADER){
                setLastUpdateDate();
            }else if (loader == ECB_90_DAYS_LOADER){
                showChart();
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

    private void showChart(){
        Cursor cursor = getContentResolver().query(HistoricalDataDbContract.HistoricalDataEntry.CONTENT_URI,null,null,null, null);
        if (cursor.moveToFirst()) {
            cursor.close();
            Intent intent = new Intent(MainActivity.this, ChartActivity.class);
            intent.putExtra(Constants.FIRST_CURRENCY,calculations.getSpinnerValue(topSpinnerValue));
            intent.putExtra(Constants.SECOND_CURRENCY,calculations.getSpinnerValue(bottomSpinnerValue));
            startActivity(intent);
        } else {
            Toast.makeText(this, "You need internet connection once, so you can get the data for the charts", Toast.LENGTH_SHORT).show();

        }
    }

    private void setLastUpdateDate(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        TextView lastUpdate = (TextView) findViewById(R.id.last_update_text_view);
        String date = "Data accurate as of "+sharedPref.getString("date","");
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
        getMenuInflater().inflate(R.menu.activity_main_menus,menu);
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

        if (id==R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
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
        final EditText editTextTop = (EditText) findViewById(R.id.edit_text_top);
        final EditText editTextBottom = (EditText) findViewById(R.id.edit_text_bottom);

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
        Spinner spinnerBottom = (Spinner) findViewById(R.id.spinner_bottom);
        Spinner spinnerTop = (Spinner) findViewById(R.id.spinner_top);
        spinnerTop.setAdapter(spinnerAdapter);
        spinnerBottom.setAdapter(spinnerAdapter);
        spinnerTop.setSelection(topSpinnerSelection);
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

        AsyncTaskLoader loader = null;
        switch (loaderId){
            case ECB_DAILY_LOADER:
                showLoading();
                loader = new ECBDailyDataLoader(this,Constants.ECB_DAILY_URL,sharedPreferences);
                break;

            case ECB_90_DAYS_LOADER:
                showLoading();
                loader = new ECBNinetyDaysDataLoader(this,Constants.ECB_90_DAYS_URL);
                break;
        }
        return loader;
    }

    private void showLoading(){
        loadingFragment = new LoadingFragment();
        loadingFragment.show(getSupportFragmentManager(), Constants.TAG_FRAGMENT);
        loadingFragment.setCancelable(false);
    }

    private void dismissLoading(){
        loadingFragment.dismiss();
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
            calculations.calculate(TOP_SPINNER, topSpinnerValue, bottomSpinnerValue);
            setLastUpdateDate();
            dismissLoading();
            Toast.makeText(this, R.string.exchange_rates_updated, Toast.LENGTH_SHORT).show();
        }else{
            dismissLoading();
            Toast.makeText(this, R.string.exchange_rates_update_failed, Toast.LENGTH_SHORT).show();
        }
    }



    private void onLoadFinishedECB90DaysLoader(DataFromServerDTO data){
        if(data.getResponseCode()==200) {
                dismissLoading();
                Toast.makeText(this, R.string.charts_data_updated, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ChartActivity.class);
                intent.putExtra(Constants.FIRST_CURRENCY,calculations.getSpinnerValue(topSpinnerValue));
                intent.putExtra(Constants.SECOND_CURRENCY,calculations.getSpinnerValue(bottomSpinnerValue));
                startActivity(intent);
        }else{
            dismissLoading();
            Toast.makeText(this, R.string.charts_data_update_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<DataFromServerDTO> loader) {
    }
}
