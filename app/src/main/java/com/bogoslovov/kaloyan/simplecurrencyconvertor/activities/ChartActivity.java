package com.bogoslovov.kaloyan.simplecurrencyconvertor.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.bogoslovov.kaloyan.simplecurrencyconvertor.R;
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

    private static final int GET_CHART_DATA = 4;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_chart);
        Intent intent = getIntent();
        Bundle currencies = new Bundle();
        currencies.putString(Constants.FIRST_CURRENCY,intent.getStringExtra(Constants.FIRST_CURRENCY));
        currencies.putString(Constants.SECOND_CURRENCY,intent.getStringExtra(Constants.SECOND_CURRENCY));
        startLoader(currencies);

    }

    private void startLoader(Bundle bundle){
        LoaderManager loaderManager = getLoaderManager();
        Loader<Object> passwordLoader = loaderManager.getLoader(GET_CHART_DATA);

        if(passwordLoader==null){
            loaderManager.initLoader(GET_CHART_DATA,bundle,this);
        }else{
            loaderManager.restartLoader(GET_CHART_DATA,bundle,this);
        }
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
        for(int i = 0; i<datesList.size(); i++){
            System.out.println(datesList.get(i)+"  "+exchangeRatesList.get(i));
        }
        Collections.reverse(datesList);
        Collections.reverse(exchangeRatesList);
        initChart(datesList,exchangeRatesList);
    }

    private void initChart(final List<String> datesList, List<Float> exchangeRatesList){
        LineChart chart = (LineChart) findViewById(R.id.chart);
        List<Entry> entries = new ArrayList<Entry>();
        float order = 0f;
        for (int i = 0; i< exchangeRatesList.size(); i++){
            entries.add(new Entry(order,exchangeRatesList.get(i)));
            System.out.println(exchangeRatesList.get(i).floatValue());
            order++;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(Color.parseColor("#ffffff"));
        dataSet.setValueTextColor(Color.parseColor("#ffffff"));

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return datesList.get((int) value);
            }
        };

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.setVisibleXRangeMaximum(16);
        chart.moveViewToX(44);
        chart.invalidate(); // refresh
    }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
