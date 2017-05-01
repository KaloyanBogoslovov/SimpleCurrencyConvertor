package com.bogoslovov.kaloyan.simplecurrencyconvertor.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bogoslovov.kaloyan.simplecurrencyconvertor.R;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.constants.Constants;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kaloyan on 24/04/2017.
 */

public class ChartActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        initChart();
    }

    private void initChart(){
        LineChart chart = (LineChart) findViewById(R.id.chart);
        List<Entry> entries = new ArrayList<Entry>();
        for (int i = 0; i< Constants.x.length-1; i++){
            entries.add(new Entry(Float.parseFloat(Constants.x[i]),Float.parseFloat(Constants.y[i])));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(Color.parseColor("#ffffff"));
        dataSet.setValueTextColor(Color.parseColor("#000000"));
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh
    }
}
