package com.bogoslovov.kaloyan.simplecurrencyconvertor;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.bogoslovov.kaloyan.simplecurrencyconvertor.data.ECBData;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.bogoslovov.kaloyan.simplecurrencyconvertor.data.ECBData.sharedPreferences;

public class MainActivity extends AppCompatActivity {
    String bottomSpinner="";
    String topSpinner="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkIfSharedPreferenceExists();
        initSpinners();
        initSwapButton();
        initEditTextFields();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkForConnection();
        System.out.println("The application started: onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("The application started: onResume");
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
    private void checkIfSharedPreferenceExists() {
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        if (!sharedPreferences.contains("EUR")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("EUR", "1");
            editor.putString("JPY", "1");
            editor.putString("BGN", "1");
            editor.putString("CZK", "1");
            editor.putString("DKK", "1");
            editor.putString("GBP", "1");
            editor.putString("HUF", "1");
            editor.putString("USD", "1");
            editor.putString("PLN", "1");
            editor.putString("RON", "1");
            editor.putString("SEK", "1");
            editor.putString("CHF", "1");
            editor.putString("NOK", "1");
            editor.putString("HRK", "1");
            editor.putString("RUB", "1");
            editor.putString("TRY", "1");
            editor.putString("AUD", "1");
            editor.putString("BRL", "1");
            editor.putString("CAD", "1");
            editor.putString("CNY", "1");
            editor.putString("HKD", "1");
            editor.putString("IDR", "1");
            editor.putString("ILS", "1");
            editor.putString("INR", "1");
            editor.putString("KRW", "1");
            editor.putString("MXN", "1");
            editor.putString("MYR", "1");
            editor.putString("NZD", "1");
            editor.putString("PHP", "1");
            editor.putString("SGD", "1");
            editor.putString("THB", "1");
            editor.putString("ZAR", "1");
            editor.commit();
        }
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
        editTextTop.setText("1");
        editTextTop.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            public void afterTextChanged(Editable editable) {
                if (editTextTop.isFocused()) {
                    if (!editTextTop.getText().toString().equals("")) {
                        calculate("top");
                    }
                    System.out.println("bottom listener activated");
                }
            }
        });

        final EditText editTextBottom = (EditText) findViewById(R.id.edit_text_bottom);
        editTextBottom.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            public void afterTextChanged(Editable editable) {
                if (editTextBottom.isFocused()) {
                    if (!editTextBottom.getText().toString().equals("")) {
                        calculate("bottom");
                    }
                    System.out.println("bottom listener activated");
                }
            }
        });
    }

    private void checkForConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() &&networkInfo.isAvailable()) {
            new ECBData().execute();
        }

    }

    private void initSpinners(){
        String [] currencies={"EUR Euro", "USD US dollar", "JPY Japanese yen", "BGN Bulgarian lev", "CZK Czech koruna",
                "DKK Danish krone", "GBP Pound sterling", "HUF Hungarian forint", "PLN Polish zloty", "RON Romanian leu",
                "SEK Swedish krona", "CHF Swiss franc", "NOK Norwegian krone", "HRK Croatian kuna", "RUB Russian rouble",
                "TRY Turkish lira", "AUD Australian dollar", "BRL Brazilian real", "CAD Canadian dollar", "CNY Chinese yuan",
                "HKD Hong Kong dollar", "IDR Indonesian rupiah", "ILS Israeli shekel", "INR Indian rupee", "KRW South Korean won",
                "MXN Mexican peso", "MYR Malaysian ringgit","NZD New Zealand dollar", "PHP Philippine peso",
                "SGD Singapore dollar", "THB Thai baht", "ZAR South African rand"};
        Integer [] images = {R.drawable.eur,R.drawable.usa,R.drawable.jpy,R.drawable.bgn,R.drawable.czk,
                R.drawable.dkk,R.drawable.gbp,R.drawable.huf,R.drawable.pln,R.drawable.ron,
                R.drawable.sek,R.drawable.chf,R.drawable.nok,R.drawable.hrk,R.drawable.rub,
                R.drawable.tryy,R.drawable.aud,R.drawable.brl,R.drawable.cad,R.drawable.cny,
                R.drawable.hkd,R.drawable.idr,R.drawable.ils,R.drawable.inr,R.drawable.krw,
                R.drawable.mxn,R.drawable.myr,R.drawable.nzd,R.drawable.php,
                R.drawable.sgd,R.drawable.thb,R.drawable.zar};

        final SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_row,currencies,images);
        Spinner spinnerBottom = (Spinner) findViewById(R.id.spinner_bottom);
        Spinner spinnerTop = (Spinner) findViewById(R.id.spinner_top);
        spinnerTop.setAdapter(spinnerAdapter);
        spinnerBottom.setAdapter(spinnerAdapter);
        spinnerBottom.setSelection(1);
        spinnerTop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                topSpinner = spinnerAdapter.getItem(position);
                calculate("top");
            }
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        spinnerBottom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                bottomSpinner = spinnerAdapter.getItem(position);
                calculate("top");
            }
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }
    public void calculate(String topOrBottom) {
        String topSpinnerValue = getSpinnerValue(topSpinner);
        String bottomSpinnerValue = getSpinnerValue(bottomSpinner);
        EditText editTextTop = (EditText) findViewById(R.id.edit_text_top);
        EditText editTextBottom= (EditText) findViewById(R.id.edit_text_bottom);
        if (topOrBottom == "top") {
            BigDecimal exchangeRate =convert(topSpinnerValue, bottomSpinnerValue);
            BigDecimal inputValue =  new BigDecimal(editTextTop.getText().toString());
            BigDecimal result = exchangeRate.multiply(inputValue).setScale(2, RoundingMode.HALF_UP);
            editTextBottom.setText(result.toString());

        } else {
            BigDecimal exchangeRate =convert(bottomSpinnerValue, topSpinnerValue);
            BigDecimal inputValue = new BigDecimal(editTextBottom.getText().toString());
            BigDecimal result = exchangeRate.multiply(inputValue).setScale(2, RoundingMode.HALF_UP);
            editTextTop.setText(result.toString());
        }
    }

    private String getSpinnerValue(String spinner){

        switch (spinner){
            case"EUR Euro": return "EUR";
            case"USD US dollar": return "USD";
            case"JPY Japanese yen": return "JPY";
            case"BGN Bulgarian lev": return "BGN";
            case"CZK Czech koruna": return "CZK";
            case"DKK Danish krone": return "DKK";
            case"GBP Pound sterling": return "GBP";
            case"HUF Hungarian forint": return "HUF";
            case"PLN Polish zloty": return "PLN";
            case"RON Romanian leu": return "RON";
            case"SEK Swedish krona": return "SEK";
            case"CHF Swiss franc": return "CHF";
            case"NOK Norwegian krone": return "NOK";
            case"HRK Croatian kuna": return "HRK";
            case"RUB Russian rouble": return "RUB";
            case"TRY Turkish lira": return "TRY";
            case"AUD Australian dollar": return "AUD";
            case"BRL Brazilian real": return "BRL";
            case"CAD Canadian dollar": return "CAD";
            case"CNY Chinese yuan": return "CNY";
            case"HKD Hong Kong dollar": return "HKD";
            case"IDR Indonesian rupiah": return "IDR";
            case"ILS Israeli shekel": return "ILS";
            case"INR Indian rupee": return "INR";
            case"KRW South Korean won": return "KRW";
            case"MXN Mexican peso": return "MXN";
            case"MYR Malaysian ringgit": return "MYR";
            case"NZD New Zealand dollar": return "NZD";
            case"PHP Philippine peso": return "PHP";
            case"SGD Singapore dollar": return "SGD";
            case"THB Thai baht": return "THB";
            case"ZAR South African rand": return "ZAR";
        }
        return "EUR";
    }

    private BigDecimal convert(String key, String target){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        BigDecimal firstValue=new BigDecimal(sharedPref.getString(key,""));
        BigDecimal secondValue=new BigDecimal(sharedPref.getString(target,""));
        BigDecimal exchangeRate = secondValue.divide(firstValue,4,RoundingMode.HALF_UP);
        return exchangeRate;
    }

}
