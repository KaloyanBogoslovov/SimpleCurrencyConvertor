package com.bogoslovov.kaloyan.simplecurrencyconvertor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Calculations {
    public static String bottomSpinner="";
    public static String topSpinner="";
    Activity activity;
    public Calculations(Activity activity){
        this.activity = activity;
    }

    public void calculate(String topOrBottom, String topSpinner, String bottomSpinner) {
        String topSpinnerValue = getSpinnerValue(topSpinner);
        String bottomSpinnerValue = getSpinnerValue(bottomSpinner);
        EditText editTextTop = (EditText) activity.findViewById(R.id.edit_text_top);
        EditText editTextBottom= (EditText) activity.findViewById(R.id.edit_text_bottom);
        if (topOrBottom == "top") {
            BigDecimal exchangeRate =convert(topSpinnerValue, bottomSpinnerValue);
            BigDecimal inputValue =  new BigDecimal(editTextTop.getText().toString());
            BigDecimal result = exchangeRate.multiply(inputValue).setScale(3, RoundingMode.HALF_UP);
            editTextBottom.setText(result.toString());

        } else {
            BigDecimal exchangeRate =convert(bottomSpinnerValue, topSpinnerValue);
            BigDecimal inputValue = new BigDecimal(editTextBottom.getText().toString());
            BigDecimal result = exchangeRate.multiply(inputValue).setScale(3, RoundingMode.HALF_UP);
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
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        BigDecimal firstValue=new BigDecimal(sharedPref.getString(key,""));
        BigDecimal secondValue=new BigDecimal(sharedPref.getString(target,""));
        BigDecimal exchangeRate = secondValue.divide(firstValue,8,RoundingMode.HALF_UP);
        return exchangeRate;
    }
}
