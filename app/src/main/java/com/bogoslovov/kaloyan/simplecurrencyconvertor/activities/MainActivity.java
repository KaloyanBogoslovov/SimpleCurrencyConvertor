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
import android.util.Log;
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

import com.bogoslovov.kaloyan.simplecurrencyconvertor.R;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.Utils;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.adapters.SpinnerAdapter;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.constants.Constants;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.db.HistoricalDataDbContract;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.dtos.DataFromServerDTO;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.fragments.LoadingFragment;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.loaders.ECBDailyDataLoader;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.loaders.ECBNinetyDaysDataLoader;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.bogoslovov.kaloyan.simplecurrencyconvertor.Utils.getSpinnerValue;
import static com.bogoslovov.kaloyan.simplecurrencyconvertor.constants.Constants.BOTTOM_SPINNER;
import static com.bogoslovov.kaloyan.simplecurrencyconvertor.constants.Constants.TOP_SPINNER;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<DataFromServerDTO> {

    private static final int ECB_DAILY_LOADER =1;
    private static final int ECB_90_DAYS_LOADER  = 2;
    private static final String TAG = MainActivity.class.getName();

    private static String bottomSpinnerCurrency ="";
    private static String topSpinnerCurrency ="";
    private static int topSpinnerSelection=0;
    private static int bottomSpinnerSelection = 1;
    private static boolean onlineMode;
    private SharedPreferences sharedPreferences;
    private LoadingFragment loadingFragment;
    private EditText editTextTop;
    private EditText editTextBottom;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initValues();
        Utils.checkIfSharedPreferenceExists(sharedPreferences);
        checkForConnection(ECB_DAILY_LOADER);
        initSpinners();
        initSwapButton();
        initShowChartButton();
        initEditTextFields();
        setWindowState();
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

    private void initValues(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        onlineMode = getDataMode();

        if (getIntent() != null && getIntent().getExtras()!=null){
            Intent intent = getIntent();
            String firstCurrency = intent.getStringExtra(Constants.FIRST_CURRENCY);
            String secondCurrency = intent.getStringExtra(Constants.SECOND_CURRENCY);
            topSpinnerSelection = Utils.getObjectNumber(Constants.currencyTags,firstCurrency);
            bottomSpinnerSelection = Utils.getObjectNumber(Constants.currencyTags, secondCurrency);
        }
    }

    private boolean getDataMode(){
        boolean online = sharedPreferences.getBoolean("online-mode",true);
        Log.i(TAG,"hfghfghf");
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                Log.i(TAG,"sharedPreference changed");
                if ("online-mode".equals(key)){
                    onlineMode = prefs.getBoolean("online-mode",true);
                }
            }
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
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

    private void setLastUpdateDate(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        TextView lastUpdate = (TextView) findViewById(R.id.last_update_text_view);
        String date = "Data accurate as of "+sharedPref.getString("date","");
        lastUpdate.setText(date);
    }

    private void showChart(){
        Cursor cursor = getContentResolver().query(HistoricalDataDbContract.HistoricalDataEntry.CONTENT_URI,null,null,null, null);
        if (cursor.moveToFirst()) {
            cursor.close();
            Intent intent = new Intent(MainActivity.this, ChartActivity.class);
            intent.putExtra(Constants.FIRST_CURRENCY,getSpinnerValue(topSpinnerCurrency));
            intent.putExtra(Constants.SECOND_CURRENCY,getSpinnerValue(bottomSpinnerCurrency));
            startActivity(intent);
        } else {
            Toast.makeText(this, "You need internet connection once, so you can get the data for the charts", Toast.LENGTH_SHORT).show();
        }
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
                topSpinnerCurrency = spinnerAdapter.getItem(position);
                topSpinnerSelection = position;
                calculate(TOP_SPINNER);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        spinnerBottom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                bottomSpinnerCurrency = spinnerAdapter.getItem(position);
                bottomSpinnerSelection = position;
                calculate(TOP_SPINNER);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
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
                    String value = editTextTop.getText().toString();
                    if (value.equals(".")){
                        editTextTop.setText("0.");
                        editTextTop.setSelection(editTextTop.getText().length());
                    }
                    else if(value.equals("")) {
                        editTextBottom.setText("0.000");
                    }else{
                        calculate(TOP_SPINNER);
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
                    String value = editTextBottom.getText().toString();
                    if(value.equals("")) {
                        editTextTop.setText("0.000");
                    }else if (value.equals(".")){
                        editTextBottom.setText("0.");
                        editTextBottom.setSelection(editTextBottom.getText().length());
                    }else{
                        calculate(BOTTOM_SPINNER);
                    }
                }
            }
        });
    }

    private void setWindowState(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setElevation(0f);
        }
    }

    @Override
    public Loader<DataFromServerDTO> onCreateLoader(int loaderId, Bundle args) {

        AsyncTaskLoader loader = null;
        switch (loaderId){
            case ECB_DAILY_LOADER:
                showLoading();
                loader = new ECBDailyDataLoader(this,sharedPreferences);
                break;

            case ECB_90_DAYS_LOADER:
                showLoading();
                loader = new ECBNinetyDaysDataLoader(this);
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
            calculate(TOP_SPINNER);
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
                intent.putExtra(Constants.FIRST_CURRENCY,getSpinnerValue(topSpinnerCurrency));
                intent.putExtra(Constants.SECOND_CURRENCY,getSpinnerValue(bottomSpinnerCurrency));
                startActivity(intent);
        }else{
            dismissLoading();
            Toast.makeText(this, R.string.charts_data_update_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<DataFromServerDTO> loader) {
    }

    public void calculate(String topOrBottom) {
        String topSpinnerValue = getSpinnerValue(topSpinnerCurrency);
        String bottomSpinnerValue = getSpinnerValue(bottomSpinnerCurrency);

        if (topOrBottom.equals(TOP_SPINNER)) {
            BigDecimal exchangeRate = getExchangeRate(topSpinnerValue, bottomSpinnerValue);
            BigDecimal inputValue =  new BigDecimal(editTextTop.getText().toString());
            BigDecimal result = exchangeRate.multiply(inputValue).setScale(3, RoundingMode.HALF_UP);
            editTextBottom.setText(result.toString());
        } else {
            BigDecimal exchangeRate = getExchangeRate(bottomSpinnerValue, topSpinnerValue);
            BigDecimal inputValue =  new BigDecimal(editTextBottom.getText().toString());
            BigDecimal result = exchangeRate.multiply(inputValue).setScale(3, RoundingMode.HALF_UP);
            editTextTop.setText(result.toString());
        }
    }

    private BigDecimal getExchangeRate(String key, String target){
        BigDecimal firstValue=new BigDecimal(sharedPreferences.getString(key,""));
        BigDecimal secondValue=new BigDecimal(sharedPreferences.getString(target,""));
        BigDecimal exchangeRate = secondValue.divide(firstValue,8,RoundingMode.HALF_UP);
        return exchangeRate;
    }
}
