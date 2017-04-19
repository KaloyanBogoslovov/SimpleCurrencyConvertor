package com.bogoslovov.kaloyan.simplecurrencyconvertor.activities;

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
import com.bogoslovov.kaloyan.simplecurrencyconvertor.Utils;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.adapters.SpinnerAdapter;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.constants.Constants;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.loaders.ECBDataLoader;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.dtos.DataFromServerDTO;

import java.io.IOException;

import static com.bogoslovov.kaloyan.simplecurrencyconvertor.constants.Constants.BOTTOM_SPINNER;
import static com.bogoslovov.kaloyan.simplecurrencyconvertor.constants.Constants.TOP_SPINNER;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<DataFromServerDTO> {

    private static final int ECB_LOADER=1;
    private static String bottomSpinnerValue ="";
    private static String topSpinnerValue ="";

    Calculations calculations = new Calculations(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForConnection();
        Utils.checkIfSharedPreferenceExists(this);
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
              Utils.parseAndSaveData(data.getBody());
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

  @Override
  public void onLoaderReset(Loader<DataFromServerDTO> loader) {
  }
}
