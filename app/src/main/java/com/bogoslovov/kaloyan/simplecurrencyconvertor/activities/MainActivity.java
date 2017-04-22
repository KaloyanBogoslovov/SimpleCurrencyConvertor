package com.bogoslovov.kaloyan.simplecurrencyconvertor.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
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
import com.bogoslovov.kaloyan.simplecurrencyconvertor.dtos.DataFromServerDTO;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.loaders.ECBDataLoader;

import java.io.BufferedReader;
import java.io.IOException;

import static com.bogoslovov.kaloyan.simplecurrencyconvertor.constants.Constants.BOTTOM_SPINNER;
import static com.bogoslovov.kaloyan.simplecurrencyconvertor.constants.Constants.TOP_SPINNER;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<DataFromServerDTO> {

    private static final int ECB_LOADER=1;
    private static String bottomSpinnerValue ="";
    private static String topSpinnerValue ="";
    private SharedPreferences sharedPreferences;
    private Calculations calculations = new Calculations(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForConnection();
        checkIfSharedPreferenceExists(this);
        initSpinners();
        initSwapButton();
        initEditTextFields();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setElevation(0f);
        }
    }

    private void checkForConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() &&networkInfo.isAvailable()) {
            startLoader();
        }else{
            setLastUpdateDate();
        }
    }

    private void startLoader(){
        LoaderManager loaderManager = getLoaderManager();
        Loader<Object> passwordLoader = loaderManager.getLoader(ECB_LOADER);

        if(passwordLoader==null){
            loaderManager.initLoader(ECB_LOADER,null,this);
        }else{
            loaderManager.restartLoader(ECB_LOADER,null,this);
        }
    }

    private void setLastUpdateDate(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        TextView lastUpdate = (TextView) findViewById(R.id.last_update_text_view);
        String date = "Last update: "+sharedPref.getString("date","");
        lastUpdate.setText(date);
    }

    private void checkIfSharedPreferenceExists(Activity activity) {
        sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
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
        spinnerBottom.setSelection(1);
        spinnerTop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                topSpinnerValue = spinnerAdapter.getItem(position);
                calculations.calculate(TOP_SPINNER, topSpinnerValue, bottomSpinnerValue);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        spinnerBottom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                bottomSpinnerValue = spinnerAdapter.getItem(position);
                calculations.calculate(TOP_SPINNER, topSpinnerValue, bottomSpinnerValue);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

  @Override
  public Loader<DataFromServerDTO> onCreateLoader(int id, Bundle args) {
        return new ECBDataLoader(this);
  }

  @Override
  public void onLoadFinished(Loader<DataFromServerDTO> loader, DataFromServerDTO data) {
      if(data.getResponseCode()==200) {
          try {
              parseAndSaveData(data.getBody());
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

    private  void parseAndSaveData(BufferedReader br) throws IOException {
        for (int i = 0; i < 7; i++) {
            br.readLine();
            System.out.println(br.toString());
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String line = br.readLine();
        String date = line.substring(14, 24);
        editor.putString("EUR", "1");
        editor.putString("date",date);
        String remaining;
        String symbol;
        String rate;
        for (int i = 0; i < 31; i++) {
            System.out.println(br.toString());
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

  @Override
  public void onLoaderReset(Loader<DataFromServerDTO> loader) {
  }
}
