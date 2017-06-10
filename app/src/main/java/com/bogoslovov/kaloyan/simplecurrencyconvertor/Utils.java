package com.bogoslovov.kaloyan.simplecurrencyconvertor;

import android.content.SharedPreferences;

/**
 * Created by kaloqn on 5/14/17.
 */

public class Utils {

    public static int getObjectNumber(String[]currencies, String currency){
        int number = 0;
        for (int i=0; i<currencies.length; i++){
            if (currencies[i].equals(currency)){
                return i;
            }
        }
        return number;
    }

    public static String getSpinnerValue(String spinner){

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

    public static void checkIfSharedPreferenceExists(SharedPreferences sharedPreferences) {
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

}
