package com.bogoslovov.kaloyan.simplecurrencyconvertor.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.bogoslovov.kaloyan.simplecurrencyconvertor.R;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.Utils;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.ValueFormatter;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.adapters.SpinnerAdapter;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.constants.Constants;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.db.HistoricalDataDbContract;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kaloyan on 24/04/2017.
 */

public class ChartActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int GET_CHART_DATA_LOADER = 4;
    private static String firstCurrency = "";
    private static String secondCurrency = "";
    private int white;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_chart);

        white  = ContextCompat.getColor(this, R.color.white);
        Intent intent = getIntent();
        firstCurrency = intent.getStringExtra(Constants.FIRST_CURRENCY);
        secondCurrency= intent.getStringExtra(Constants.SECOND_CURRENCY);

        startLoader();
        setUpToolbar();
        initSpinners();
        initSwapButton();
    }

    private void startLoader(){
        Bundle currencies = new Bundle();
        currencies.putString(Constants.FIRST_CURRENCY,firstCurrency);
        currencies.putString(Constants.SECOND_CURRENCY,secondCurrency);
        LoaderManager loaderManager = getLoaderManager();
        Loader<Object> passwordLoader = loaderManager.getLoader(GET_CHART_DATA_LOADER);

        if(passwordLoader==null){
            loaderManager.initLoader(GET_CHART_DATA_LOADER,currencies,this);
        }else{
            loaderManager.restartLoader(GET_CHART_DATA_LOADER,currencies,this);
        }
    }

    private void setUpToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.chart_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChartActivity.this, MainActivity.class);
                intent.putExtra(Constants.FIRST_CURRENCY,firstCurrency);
                intent.putExtra(Constants.SECOND_CURRENCY, secondCurrency);
                startActivity(intent);
            }
        });
    }

    private void initSpinners(){

        final SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_row,Constants.currencyTags,new Constants().images);
        Spinner secondCurrencySpinner = (Spinner) findViewById(R.id.chart_second_currency);
        Spinner firstCurrencySpinner = (Spinner) findViewById(R.id.chart_first_currency);
        firstCurrencySpinner.setAdapter(spinnerAdapter);
        secondCurrencySpinner.setAdapter(spinnerAdapter);
        firstCurrencySpinner.setSelection(Utils.getObjectNumber(Constants.currencyTags, firstCurrency));
        secondCurrencySpinner.setSelection(Utils.getObjectNumber(Constants.currencyTags,secondCurrency));

        firstCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                firstCurrency = spinnerAdapter.getItem(position);
                startLoader();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        secondCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                secondCurrency = spinnerAdapter.getItem(position);
                startLoader();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void initSwapButton(){
        ImageButton swapButton = (ImageButton) findViewById(R.id.chart_swap_currencies);
        swapButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Spinner secondCurrencySpinner = (Spinner) findViewById(R.id.chart_second_currency);
                Spinner firstCurrencySpinner = (Spinner) findViewById(R.id.chart_first_currency);
                int position = secondCurrencySpinner.getSelectedItemPosition();
                secondCurrencySpinner.setSelection(firstCurrencySpinner.getSelectedItemPosition());
                firstCurrencySpinner.setSelection(position);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle currencies) {
        Uri chartUri = HistoricalDataDbContract.HistoricalDataEntry.buildChartDataUri(currencies.getString(Constants.FIRST_CURRENCY),currencies.getString(Constants.SECOND_CURRENCY));
        return new CursorLoader(this, chartUri, null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<String> datesList = new ArrayList<>();
        List<Float> exchangeRatesList = new ArrayList<>();
        BigDecimal exchangeRate;
        while (data.moveToNext()){
            datesList.add(data.getString(2));
            exchangeRate = new BigDecimal(data.getString(1)).divide(new BigDecimal(data.getString(0)),2,BigDecimal.ROUND_HALF_EVEN);
            exchangeRatesList.add(Float.parseFloat(exchangeRate.toPlainString()));
        }
        Collections.reverse(datesList);
        Collections.reverse(exchangeRatesList);
        initChart(datesList,exchangeRatesList);
    }

    private void initChart(final List<String> datesList, List<Float> exchangeRatesList){

        LineChart chart = (LineChart) findViewById(R.id.chart);
        chart.setData(getLineData(exchangeRatesList));

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(getFormatter(datesList));
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.setDescription(null);
        chart.getLegend().setEnabled(false);
        chart.getAxisLeft().setTextColor(white);
        chart.getAxisRight().setTextColor(white);
        chart.getXAxis().setTextColor(white);
        chart.setVisibleXRangeMaximum(16);
        chart.moveViewToX(44);
        chart.invalidate();
    }

    private LineData getLineData(List<Float> exchangeRatesList){
        List<Entry> entries = new ArrayList<>();
        float order = 0f;
        for (int i = 0; i< exchangeRatesList.size(); i++){
            entries.add(new Entry(order,exchangeRatesList.get(i)));
            order++;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(white);
        dataSet.setValueTextColor(white);

        LineData lineData = new LineData(dataSet);
        lineData.setValueFormatter(new ValueFormatter());

        return lineData;
    }

    private IAxisValueFormatter getFormatter(final List<String> datesList){
        return new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return datesList.get((int) value);
            }
        };
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
