package com.bogoslovov.kaloyan.simplecurrencyconvertor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.bogoslovov.kaloyan.simplecurrencyconvertor.Constants.TOP_SPINNER;

public class Calculations {
    private Activity activity;
    public Calculations(Activity activity){
        this.activity = activity;
    }

    public void calculate(String topOrBottom, String topSpinner, String bottomSpinner) {
        String topSpinnerValue = Utils.getSpinnerValue(topSpinner);
        String bottomSpinnerValue = Utils.getSpinnerValue(bottomSpinner);
        EditText editTextTop = (EditText) activity.findViewById(R.id.edit_text_top);
        EditText editTextBottom= (EditText) activity.findViewById(R.id.edit_text_bottom);

        if (topOrBottom.equals(TOP_SPINNER)) {
            BigDecimal exchangeRate =convert(topSpinnerValue, bottomSpinnerValue);
            BigDecimal inputValue =  new BigDecimal(editTextTop.getText().toString());
            BigDecimal result = exchangeRate.multiply(inputValue).setScale(3, RoundingMode.HALF_UP);
            editTextBottom.setText(result.toString());
        } else {
            BigDecimal exchangeRate =convert(bottomSpinnerValue, topSpinnerValue);
            BigDecimal inputValue =  new BigDecimal(editTextBottom.getText().toString());
            BigDecimal result = exchangeRate.multiply(inputValue).setScale(3, RoundingMode.HALF_UP);
            editTextTop.setText(result.toString());
        }
    }

    private BigDecimal convert(String key, String target){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        BigDecimal firstValue=new BigDecimal(sharedPref.getString(key,""));
        BigDecimal secondValue=new BigDecimal(sharedPref.getString(target,""));
        BigDecimal exchangeRate = secondValue.divide(firstValue,8,RoundingMode.HALF_UP);
        return exchangeRate;
    }
}
