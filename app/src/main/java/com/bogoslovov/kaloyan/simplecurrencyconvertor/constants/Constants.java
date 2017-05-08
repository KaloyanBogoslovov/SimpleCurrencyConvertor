package com.bogoslovov.kaloyan.simplecurrencyconvertor.constants;

import com.bogoslovov.kaloyan.simplecurrencyconvertor.R;

/**
 * Created by Kaloyan on 17.1.2017 г..
 */

public class Constants {
    public static final String ECB_DAILY_URL = "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
    public static final String ECB_90_DAYS_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml";
    public static final String TOP_SPINNER = "top";
    public static final String BOTTOM_SPINNER="bottom";
    public static final String FIRST_CURRENCY = "first-currency";
    public static final String SECOND_CURRENCY = "second-currency";

    public final String [] currencies={"EUR Euro", "USD US dollar", "JPY Japanese yen", "BGN Bulgarian lev", "CZK Czech koruna",
        "DKK Danish krone", "GBP Pound sterling", "HUF Hungarian forint", "PLN Polish zloty", "RON Romanian leu",
        "SEK Swedish krona", "CHF Swiss franc", "NOK Norwegian krone", "HRK Croatian kuna", "RUB Russian rouble",
        "TRY Turkish lira", "AUD Australian dollar", "BRL Brazilian real", "CAD Canadian dollar", "CNY Chinese yuan",
        "HKD Hong Kong dollar", "IDR Indonesian rupiah", "ILS Israeli shekel", "INR Indian rupee", "KRW South Korean won",
        "MXN Mexican peso", "MYR Malaysian ringgit","NZD New Zealand dollar", "PHP Philippine peso",
        "SGD Singapore dollar", "THB Thai baht", "ZAR South African rand"};

    public final Integer [] images = {R.drawable.eur,R.drawable.usa,R.drawable.jpy,R.drawable.bgn,R.drawable.czk,
        R.drawable.dkk,R.drawable.gbp,R.drawable.huf,R.drawable.pln,R.drawable.ron,
        R.drawable.sek,R.drawable.chf,R.drawable.nok,R.drawable.hrk,R.drawable.rub,
        R.drawable.tryy,R.drawable.aud,R.drawable.brl,R.drawable.cad,R.drawable.cny,
        R.drawable.hkd,R.drawable.idr,R.drawable.ils,R.drawable.inr,R.drawable.krw,
        R.drawable.mxn,R.drawable.myr,R.drawable.nzd,R.drawable.php,
        R.drawable.sgd,R.drawable.thb,R.drawable.zar};

    //test data

    public static String[] x = {"1.5","1.8","1.3","0.6","0.8","1.5","2","2.2","0.5","0.6","0.5","0.2"};
    public static String[] y = {"3","3.5","3.4","3.6","2","1.5","1.6","1.3","1.4","0.5","0.1","0.5"};

}
