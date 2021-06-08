package com.surefiz.screens.singlechart;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.highsoft.highcharts.common.hichartsclasses.HIOptions;
import com.highsoft.highcharts.core.HIChartView;
import com.surefiz.R;

public class SingleChartActivity extends AppCompatActivity {

    private HIChartView highChartView;
    private HIOptions hiOptions = new HIOptions();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chart);
        highChartView = (HIChartView)findViewById(R.id.highChartView);
        highChartView.setOptions(hiOptions);


    }
}
