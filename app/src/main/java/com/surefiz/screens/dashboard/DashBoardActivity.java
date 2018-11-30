package com.surefiz.screens.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.highsoft.highcharts.common.HIColor;
import com.highsoft.highcharts.common.HIGradient;
import com.highsoft.highcharts.common.hichartsclasses.HIArea;
import com.highsoft.highcharts.common.hichartsclasses.HICSSObject;
import com.highsoft.highcharts.common.hichartsclasses.HIChart;
import com.highsoft.highcharts.common.hichartsclasses.HIColumn;
import com.highsoft.highcharts.common.hichartsclasses.HICondition;
import com.highsoft.highcharts.common.hichartsclasses.HIData;
import com.highsoft.highcharts.common.hichartsclasses.HIDataLabels;
import com.highsoft.highcharts.common.hichartsclasses.HIDrilldown;
import com.highsoft.highcharts.common.hichartsclasses.HIExporting;
import com.highsoft.highcharts.common.hichartsclasses.HIHover;
import com.highsoft.highcharts.common.hichartsclasses.HILabel;
import com.highsoft.highcharts.common.hichartsclasses.HILabels;
import com.highsoft.highcharts.common.hichartsclasses.HILegend;
import com.highsoft.highcharts.common.hichartsclasses.HILine;
import com.highsoft.highcharts.common.hichartsclasses.HIMarker;
import com.highsoft.highcharts.common.hichartsclasses.HIOptions;
import com.highsoft.highcharts.common.hichartsclasses.HIOptions3d;
import com.highsoft.highcharts.common.hichartsclasses.HIPie;
import com.highsoft.highcharts.common.hichartsclasses.HIPlotOptions;
import com.highsoft.highcharts.common.hichartsclasses.HIResponsive;
import com.highsoft.highcharts.common.hichartsclasses.HIRules;
import com.highsoft.highcharts.common.hichartsclasses.HISeries;
import com.highsoft.highcharts.common.hichartsclasses.HISpline;
import com.highsoft.highcharts.common.hichartsclasses.HIStates;
import com.highsoft.highcharts.common.hichartsclasses.HIStyle;
import com.highsoft.highcharts.common.hichartsclasses.HISubtitle;
import com.highsoft.highcharts.common.hichartsclasses.HITitle;
import com.highsoft.highcharts.common.hichartsclasses.HITooltip;
import com.highsoft.highcharts.common.hichartsclasses.HIXAxis;
import com.highsoft.highcharts.common.hichartsclasses.HIYAxis;
import com.highsoft.highcharts.core.HIChartView;
import com.highsoft.highcharts.core.HIFunction;
import com.surefiz.R;
import com.surefiz.sharedhandler.LoginShared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class DashBoardActivity extends BaseActivity {

    public View view;
    HIChartView chartView, chartViewLoss, chartViewBmi, chartViewGoals, chartViewSubGoals, chartViewAchiGoals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_dash_board, null);
        addContentView(view);
        viewBind();
        showGoalsAndAcheivementsChart();
        showGoalsChart();
        setWeightChart();
        setWeightLossChart();
        setBMIChart();
        setSubGoalsChart();
        setHeaderView();
    }

    private void viewBind() {
        chartView = (HIChartView) findViewById(R.id.hc_weight);
        chartViewLoss = (HIChartView) findViewById(R.id.hc_weight_loss);
        chartViewBmi = (HIChartView) findViewById(R.id.hc_bmi);
        chartViewGoals = (HIChartView) findViewById(R.id.hc_goals);
        chartViewSubGoals = (HIChartView) findViewById(R.id.hc_sub_goals);
        chartViewAchiGoals = (HIChartView) findViewById(R.id.hc_achi_goals);
    }

    private void setSubGoalsChart() {
        HIOptions options = new HIOptions();

        HIChart chart = new HIChart();
        chart.setBackgroundColor(HIColor.initWithHexValue("#000000"));
        chart.setType("column");
        HILegend hiLegend = new HILegend();
        HICSSObject hicssObject = new HICSSObject();
        hicssObject.setColor("#ffffff");
        hiLegend.setItemStyle(hicssObject);
        options.setLegend(hiLegend);
        HIOptions3d hiOptions3d = new HIOptions3d();
        hiOptions3d.setEnabled(true);
        hiOptions3d.setAlpha(15);
        hiOptions3d.setBeta(15);
        hiOptions3d.setViewDistance(25);
        hiOptions3d.setDepth(40);
        options.setChart(chart);

        HITitle title = new HITitle();
        title.setUseHTML(true);
        title.setText("<p style='color: #ffffff; text-align: center;'>Total fruit consumption, grouped by gender</p>");
        options.setTitle(title);

        HIXAxis xAxis = new HIXAxis();
        String[] categoriesList = new String[]{"Apples", "Oranges", "Pears", "Grapes", "Bananas"};
        xAxis.setCategories(new ArrayList<>(Arrays.asList(categoriesList)));
        HILabels hiLabels = new HILabels();
        hiLabels.setStyle(hicssObject);
        xAxis.setLabels(hiLabels);
        hiLabels.setSkew3d(true);
        HIStyle hiStyle = new HIStyle();
        hiStyle.setFontSize("16px");
        options.setXAxis(new ArrayList<HIXAxis>() {{
            add(xAxis);
        }});

        HIYAxis yAxis = new HIYAxis();
        yAxis.setAllowDecimals(false);
        yAxis.setMin(0);
        HITitle hiTitle = new HITitle();
        hiTitle.setText("<p style='color: #ffffff; '>Number of fruits</p>");
        hiTitle.setSkew3d(true);
        hiLabels.setStyle(hicssObject);
        yAxis.setTitle(hiTitle);
        yAxis.setLabels(hiLabels);
        options.setYAxis(new ArrayList<HIYAxis>() {{
            add(yAxis);
        }});

        HITooltip tooltip = new HITooltip();
        tooltip.setHeaderFormat("<b>{point.key}</b><br>");
        tooltip.setPointFormat("<span style=\"color:{series.color}\"></span> {series.name}: {point.y} / {point.stackTotal}");
        options.setTooltip(tooltip);

        HIPlotOptions plotOptions = new HIPlotOptions();
        HIColumn hiColumn = new HIColumn();
        hiColumn.setStacking("normal");
        hiColumn.setDepth(40);
        options.setPlotOptions(plotOptions);

        HIExporting exporting = new HIExporting();
        exporting.setEnabled(false);
        options.setExporting(exporting);

        HIColumn series1 = new HIColumn();
        series1.setName("John");
        Number[] series1_data = new Number[]{5, 3, 4, 7, 2};
        series1.setData(new ArrayList<>(Arrays.asList(series1_data)));
        series1.setStack("male");
        HIColumn series2 = new HIColumn();
        series2.setName("Joe");
        Number[] series2_data = new Number[]{3, 4, 4, 2, 5};
        series2.setData(new ArrayList<>(Arrays.asList(series2_data)));
        series2.setStack("male");
        HIColumn series3 = new HIColumn();
        series3.setName("Jane");
        Number[] series3_data = new Number[]{2, 5, 6, 2, 1};
        series3.setData(new ArrayList<>(Arrays.asList(series3_data)));
        series3.setStack("female");
        HIColumn series4 = new HIColumn();
        series4.setName("Janet");
        Number[] series4_data = new Number[]{3, 0, 4, 4, 3};
        series4.setData(new ArrayList<>(Arrays.asList(series4_data)));
        series4.setStack("female");
        options.setSeries(new ArrayList<>(Arrays.asList(series1, series2, series3, series4)));


        chartViewSubGoals.setOptions(options);
    }

    private void showGoalsChart() {
        chartView.plugins = new ArrayList<>(Arrays.asList("drilldown"));

        HIOptions options = new HIOptions();

        HIChart chart = new HIChart();
        chart.setBackgroundColor(HIColor.initWithHexValue("#000000"));
        HILegend hiLegend = new HILegend();
        HICSSObject hicssObject = new HICSSObject();
        hicssObject.setColor("#ffffff");
        options.setLegend(hiLegend);
        chart.setType("column");
        options.setChart(chart);

        HITitle title = new HITitle();
        title.setUseHTML(true);
        title.setText("<p style='color: #ffffff; text-align: center;'>Browser market shares. January, 2015 to May, 2015</p>");
        options.setTitle(title);

//        HISubtitle subtitle = new HISubtitle();
//        subtitle.setText("Click the columns to view versions.'Source': <a href=\"http://netmarketshare.com\">netmarketshare.com</a>.");
//        options.setSubtitle(subtitle);

        HIXAxis xAxis = new HIXAxis();
        xAxis.setType("category");
        HILabels labels = new HILabels();
        labels.setStyle(hicssObject);
        xAxis.setLabels(labels);
        options.setXAxis(new ArrayList<HIXAxis>() {{
            add(xAxis);
        }});

        HIYAxis yAxis = new HIYAxis();
        HITitle hiTitle = new HITitle();
        HILabels hiLabels = new HILabels();
        hiTitle.setText("<p style='color: #ffffff; '>Total percent market share</p>");
        hiLabels.setStyle(hicssObject);
        yAxis.setTitle(hiTitle);
        yAxis.setLabels(hiLabels);
        options.setYAxis(new ArrayList<HIYAxis>() {{
            add(yAxis);
        }});

        HILegend legend = new HILegend();
        legend.setEnabled(false);
        options.setLegend(legend);

        HIExporting exporting = new HIExporting();
        exporting.setEnabled(false);
        options.setExporting(exporting);

        HIPlotOptions plotOptions = new HIPlotOptions();
        HISeries hiSeries = new HISeries();
        hiSeries.setBorderWidth(hiSeries);
        HIDataLabels hiDataLabels = new HIDataLabels();
        hiDataLabels.setEnabled(true);
        hiDataLabels.setFormat("{point.y:.1f}%");
        options.setPlotOptions(plotOptions);

        HITooltip tooltip = new HITooltip();
        tooltip.setHeaderFormat("<span style=\"font-size:11px\">{series.name}</span><br>");
        tooltip.setPointFormat("<span style=\"color:{point.color}\">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>");
        options.setTooltip(tooltip);

        HIColumn series1 = new HIColumn();
        series1.setName("Brands");
        series1.setColorByPoint(true);

        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("name", "Microsoft Internet Explorer");
        map1.put("y", 56.33);
        map1.put("drilldown", "Microsoft Internet Explorer");

        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("name", "Chrome");
        map2.put("y", 24.03);
        map2.put("drilldown", "Chrome");

        HashMap<String, Object> map3 = new HashMap<>();
        map3.put("name", "Firefox");
        map3.put("y", 10.38);
        map3.put("drilldown", "Firefox");

        HashMap<String, Object> map4 = new HashMap<>();
        map4.put("name", "Safari");
        map4.put("y", 4.77);
        map4.put("drilldown", "Safari");

        HashMap<String, Object> map5 = new HashMap<>();
        map5.put("name", "Opera");
        map5.put("y", 0.91);
        map5.put("drilldown", "Opera");

        HashMap<String, Object> map6 = new HashMap<>();
        map6.put("name", "Proprietary or Undetectable");
        map6.put("y", 0.2);
        map6.put("drilldown", null);

        HashMap[] series1_data = new HashMap[]{map1, map2, map3, map4, map5, map6};
        series1.setData(new ArrayList<>(Arrays.asList(series1_data)));
        options.setSeries(new ArrayList<>(Arrays.asList(series1)));

        HIDrilldown drilldown = new HIDrilldown();

        HIColumn series2 = new HIColumn();
        series2.setName("Microsoft Internet Explorer");
        series2.setId("Microsoft Internet Explorer");

        Object[] object1 = new Object[]{"v11.0", 24.13};
        Object[] object2 = new Object[]{"v8.0", 17.2};
        Object[] object3 = new Object[]{"v9.0", 8.11};
        Object[] object4 = new Object[]{"v10.0", 5.33};
        Object[] object5 = new Object[]{"v6.0", 1.06};
        Object[] object6 = new Object[]{"v7.0", 0.5};

        series2.setData(new ArrayList<>(Arrays.asList(object1, object2, object3, object4, object5, object6)));

        HIColumn series3 = new HIColumn();
        series3.setName("Chrome");
        series3.setId("Chrome");

        Object[] object7 = new Object[]{"v41.0", 4.32};
        Object[] object8 = new Object[]{"v42.0", 3.68};
        Object[] object9 = new Object[]{"v39.0", 2.96};
        Object[] object10 = new Object[]{"v36.0", 2.53};
        Object[] object11 = new Object[]{"v43.0", 1.45};
        Object[] object12 = new Object[]{"v31.0", 1.24};
        Object[] object13 = new Object[]{"v35.0", 0.85};
        Object[] object14 = new Object[]{"v38.0", 0.6};
        Object[] object15 = new Object[]{"v32.0", 0.55};
        Object[] object16 = new Object[]{"v37.0", 0.38};
        Object[] object17 = new Object[]{"v33.0", 0.19};
        Object[] object18 = new Object[]{"v34.0", 0.14};
        Object[] object19 = new Object[]{"v30.0", 0.14};
        Object[] object20 = new Object[]{"v40.0", 5};

        series3.setData(new ArrayList<>(Arrays.asList(object7, object8, object9, object10, object11, object12, object13, object14, object15, object16, object17, object18, object19, object20)));

        HIColumn series4 = new HIColumn();
        series4.setName("Firefox");
        series4.setId("Firefox");

        Object[] object21 = new Object[]{"v35", 2.76};
        Object[] object22 = new Object[]{"v36", 2.32};
        Object[] object23 = new Object[]{"v37", 2.31};
        Object[] object24 = new Object[]{"v34", 1.27};
        Object[] object25 = new Object[]{"v38", 1.02};
        Object[] object26 = new Object[]{"v31", 0.33};
        Object[] object27 = new Object[]{"v33", 0.22};
        Object[] object28 = new Object[]{"v32", 0.15};

        series4.setData(new ArrayList<>(Arrays.asList(object21, object22, object23, object24, object25, object26, object27, object28)));

        HIColumn series5 = new HIColumn();
        series5.setName("Safari");
        series5.setId("Safari");

        Object[] object29 = new Object[]{"v8.0", 2.56};
        Object[] object30 = new Object[]{"v7.1", 0.77};
        Object[] object31 = new Object[]{"v5.1", 0.42};
        Object[] object32 = new Object[]{"v5.0", 0.3};
        Object[] object33 = new Object[]{"v6.1", 0.29};
        Object[] object34 = new Object[]{"v7.0", 0.26};
        Object[] object35 = new Object[]{"v6.2", 0.17};

        series5.setData(new ArrayList<>(Arrays.asList(object29, object30, object31, object32, object33, object34, object35)));

        HIColumn series6 = new HIColumn();
        series6.setName("Opera");
        series6.setId("Opera");

        Object[] object36 = new Object[]{"v12.x", 0.34};
        Object[] object37 = new Object[]{"v28", 0.24};
        Object[] object38 = new Object[]{"v27", 0.17};
        Object[] object39 = new Object[]{"v29", 0.16};

        series6.setData(new ArrayList<>(Arrays.asList(object36, object37, object38, object39)));

        HIColumn[] seriesList = new HIColumn[]{series2, series3, series4, series5, series6};
        drilldown.setSeries(new ArrayList<>(Arrays.asList(seriesList)));
        options.setDrilldown(drilldown);


        chartViewGoals.setOptions(options);
    }

    private void setBMIChart() {
        chartView.plugins = new ArrayList<>(Arrays.asList("series-label"));

        HIOptions options = new HIOptions();

        HIChart chart = new HIChart();
        chart.setBackgroundColor(HIColor.initWithHexValue("#000000"));
        chart.setType("spline");
        HILegend hiLegend = new HILegend();
        HICSSObject hicssObject = new HICSSObject();
        hicssObject.setColor("#ffffff");
        hiLegend.setItemStyle(hicssObject);
        options.setLegend(hiLegend);
        options.setChart(chart);

        HITitle title = new HITitle();
        title.setUseHTML(true);
        title.setText("<p style='color: #ffffff; text-align: center;'>Monthly Average Temperature</p>");
        options.setTitle(title);

        HISubtitle subtitle = new HISubtitle();
        subtitle.setText("<p style='color: #ffffff; text-align: center;'>Source: WorldClimate.com</p>");
        options.setSubtitle(subtitle);

        HIXAxis xAxis = new HIXAxis();
        HILabels labels = new HILabels();
        String[] categoriesList = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        xAxis.setCategories(new ArrayList<>(Arrays.asList(categoriesList)));
        labels.setStyle(hicssObject);
        xAxis.setLabels(labels);
        options.setXAxis(new ArrayList<HIXAxis>() {{
            add(xAxis);
        }});

        HIYAxis yAxis = new HIYAxis();
        HITitle hiTitle = new HITitle();
        hiTitle.setText("<p style='color: #ffffff; '>Temperature</p>");
        HILabels hiLabels = new HILabels();
        HIFunction hiFunction = new HIFunction("function () { return this.value + ''; }");
        hiLabels.setFormatter(hiFunction);
        hiLabels.setStyle(hicssObject);
        yAxis.setTitle(hiTitle);
        yAxis.setLabels(hiLabels);
        options.setYAxis(new ArrayList<HIYAxis>() {{
            add(yAxis);
        }});

        HITooltip tooltip = new HITooltip();
        tooltip.setShared(true);
        options.setTooltip(tooltip);

        HIPlotOptions plotOptions = new HIPlotOptions();
        HISpline hiSpline = new HISpline();
        HIMarker hiMarker = new HIMarker();
        hiMarker.setRadius(4);
        hiMarker.setLineColor(HIColor.initWithHexValue("666666"));
        hiMarker.setLineWidth(1);

        HIExporting exporting = new HIExporting();
        exporting.setEnabled(false);
        options.setExporting(exporting);

        options.setPlotOptions(plotOptions);

        HISpline series1 = new HISpline();
        series1.setName("Tokyo");
        HIMarker hiMarker1 = new HIMarker();
        hiMarker1.setSymbol("square");
        HIData data1 = new HIData();
        data1.setY(26.5);
        HIMarker hiMarker2 = new HIMarker();
        hiMarker2.setSymbol("url(https://www.highcharts.com/samples/graphics/sun.png)");

        Object[] series1_data = new Object[]{7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, data1, 23.3, 18.3, 13.9, 9.6};
        series1.setData(new ArrayList<>(Arrays.asList(series1_data)));

        HISpline series2 = new HISpline();
        series2.setName("London");
        HIMarker hiMarker3 = new HIMarker();
        hiMarker3.setSymbol("diamond");
        HIData data2 = new HIData();
        data2.setY(3.9);
        HIMarker hiMarker4 = new HIMarker();
        hiMarker4.setSymbol("url(https://www.highcharts.com/samples/graphics/snow.png)");

        Object[] series2_data = new Object[]{data2, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8};
        series2.setData(new ArrayList<>(Arrays.asList(series2_data)));

        options.setSeries(new ArrayList<>(Arrays.asList(series1, series2)));


        chartViewBmi.setOptions(options);
    }

    private void setWeightLossChart() {
        HIOptions options = new HIOptions();

        HIChart chart = new HIChart();
//        chart.setPlotBackgroundColor(HIColor.initWithHexValue("#000000"));
//        chart.setBackgroundColor(HIColor.initWithHexValue("#000000"));

        chart.setPlotShadow(false);
        chart.setType("pie");
        options.setChart(chart);

        ArrayList<String> colors = new ArrayList<>();
        HIGradient hiGradient = new HIGradient((float) 0.5, (float) 0.3, (float) 0.7);

        HashMap<String, Number> radialGradient = new HashMap<>();
        radialGradient.put("cx", 0.5);
        radialGradient.put("cy", 0.3);
        radialGradient.put("r", 0.7);

        colors.add("#7cb5ec");
        colors.add("#434348");
        colors.add("#90ed7d");
        colors.add("#8085e9");

        options.setColors(colors);

        HIExporting exporting = new HIExporting();
        exporting.setEnabled(false);
        options.setExporting(exporting);

        HITitle title = new HITitle();
        title.setText("Browser market shares. January, 2015 to May, 2015");
        options.setTitle(title);

        HITooltip tooltip = new HITooltip();
        tooltip.setPointFormat("{series.name}: <b>{point.percentage:.1f}%</b>");
        options.setTooltip(tooltip);

        HIPlotOptions plotoptions = new HIPlotOptions();
        HIPie hiPie = new HIPie();
        hiPie.setAllowPointSelect(true);
        hiPie.setCursor("pointer");
        HIDataLabels hiDataLabels = new HIDataLabels();
        hiDataLabels.setEnabled(true);
        hiDataLabels.setFormat("<b>{point.name}</b>: {point.percentage:.1f} %");
        HIStyle hiStyle = new HIStyle();
        hiStyle.setColor("black");
        hiDataLabels.setConnectorColor(HIColor.initWithHexValue("#808080"));
        options.setPlotOptions(plotoptions);

        HIPie pie = new HIPie();
        pie.setName("Brands");
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("name", "Microsoft Internet Explorer");
        map1.put("y", 56.33);

        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("name", "Chrome");
        map2.put("y", 24.03);
        map2.put("sliced", true);
        map2.put("selected", true);

        HashMap<String, Object> map3 = new HashMap<>();
        map3.put("name", "Firefox");
        map3.put("y", 10.38);

        HashMap<String, Object> map4 = new HashMap<>();
        map4.put("name", "Safari");
        map4.put("y", 4.77);

        HashMap<String, Object> map5 = new HashMap<>();
        map5.put("name", "Opera");
        map5.put("y", 0.91);

        HashMap<String, Object> map6 = new HashMap<>();
        map6.put("name", "Proprietary or Undetectable");
        map6.put("y", 0.2);

        pie.setData(new ArrayList<>(Arrays.asList(map1, map2, map3, map4, map5, map6)));

        options.setSeries(new ArrayList<>(Collections.singletonList(pie)));

        chartViewLoss.setOptions(options);
    }

    private void setWeightChart() {

        HIOptions options = new HIOptions();

        HIChart chart = new HIChart();
        chart.setType("area");
        chart.setBackgroundColor(HIColor.initWithHexValue("#000000"));
        options.setChart(chart);
        HILegend hiLegend = new HILegend();
        HICSSObject hicssObject = new HICSSObject();
        hicssObject.setColor("#ffffff");
        hiLegend.setItemStyle(hicssObject);
        options.setLegend(hiLegend);

        HITitle title = new HITitle();
        title.setUseHTML(true);
        title.setText("<p style='color: #ffffff; text-align: center;'>US and USSR nuclear stockpiles</p>");
        options.setTitle(title);

//        HISubtitle subtitle = new HISubtitle();
//        subtitle.setText("<p style='color: #ffffff;'>Source: <a style='color: #ffffff;' href=\"http://thebulletin.metapress.com/content/c4120650912x74k7/fulltext.pdf\">thebulletin.metapress.com</a></p>");
//        options.setSubtitle(subtitle);

        HIXAxis xAxis = new HIXAxis();
        xAxis.setAllowDecimals(false);
        HILabels labels = new HILabels();
        HIFunction hiFunction = new HIFunction
                ("function () { return this.value; /*clean, unformatted number for year*/ }");
        labels.setFormatter(hiFunction);
        labels.setStyle(hicssObject);
        xAxis.setLabels(labels);
        options.setXAxis(new ArrayList<HIXAxis>() {{
            add(xAxis);
        }});

        HIYAxis yAxis = new HIYAxis();
        HITitle hiTitle = new HITitle();
        hiTitle.setUseHTML(true);
        hiTitle.setText("<p style='color: #ffffff; '>Nuclear weapon states</p>");
        HILabels hiLabels = new HILabels();
        HIFunction hiFunction1 = new HIFunction("function () { return this.value / 1000 + 'k'; }");
        hiLabels.setFormatter(hiFunction1);
        hiLabels.setStyle(hicssObject);
        yAxis.setTitle(hiTitle);
        yAxis.setLabels(hiLabels);
        options.setYAxis(new ArrayList<HIYAxis>() {{
            add(yAxis);
        }});

        HITooltip tooltip = new HITooltip();
        tooltip.setPointFormat("{series.name} produced <b>{point.y:,.0f}</b><br/>warheads in {point.x}");
        options.setTooltip(tooltip);

        HIExporting exporting = new HIExporting();
        exporting.setEnabled(false);
        options.setExporting(exporting);

        HIPlotOptions plotOptions = new HIPlotOptions();
        HIArea hiArea = new HIArea();
        hiArea.setPointStart(1940);
        HIMarker marker = new HIMarker();
        marker.setEnabled(false);
        marker.setSymbol("circle");
        marker.setRadius(2);
        HIStates hiStates = new HIStates();
        HIHover hiHover = new HIHover();
        hiStates.setHover(hiHover);
        hiHover.setEnabled(true);
        options.setPlotOptions(plotOptions);

        HIArea series1 = new HIArea();
        series1.setName("USA");
        Number[] series1_data = new Number[]{null, null, null, null, null, 6, 11, 32, 110, 235, 369, 640, 1005, 1436, 2063, 3057, 4618, 6444, 9822, 15468, 20434, 24126, 27387, 29459, 31056, 31982, 32040, 31233, 29224, 27342, 26662, 26956, 27912, 28999, 28965, 27826, 25579, 25722, 24826, 24605, 24304, 23464, 23708, 24099, 24357, 24237, 24401, 24344, 23586, 22380, 21004, 17287, 14747, 13076, 12555, 12144, 11009, 10950, 10871, 0};
        series1.setData(new ArrayList<>(Arrays.asList(series1_data)));
        /*HIArea series2 = new HIArea();
        series2.setName("USSR/Russia");
        Number[] series2_data = new Number[]{null, null, null, null, null, null, null, null, null, null, 5, 25, 50, 120, 150, 200, 426, 660, 869, 1060, 1605, 2471, 3322, 4238, 5221, 6129, 7089, 8339, 9399, 10538, 11643, 13092, 14478, 15915, 17385, 19055, 21205, 23044, 25393, 27935, 30062, 32049, 33952, 35804, 37431, 39197, 45000, 43000, 41000, 39000, 37000, 35000, 33000, 31000, 29000, 27000, 25000, 24000, 23000, 22000, 21000, 20000, 19000, 18000, 18000, 17000, 16000};
        series2.setData(new ArrayList<>(Arrays.asList(series2_data)));*/
        options.setSeries(new ArrayList<>(Arrays.asList(series1/*, series2*/)));


        chartView.setOptions(options);
    }


    private void showGoalsAndAcheivementsChart() {

        HIOptions options = new HIOptions();

        HIChart chart = new HIChart();
        chart.setType("line");
        chart.setBackgroundColor(HIColor.initWithHexValue("#000000"));
        options.setChart(chart);
        HICSSObject hicssObject = new HICSSObject();
        hicssObject.setColor("#ffffff");

        HITitle title = new HITitle();
        title.setUseHTML(true);
        title.setText("<p style='color: #ffffff; text-align: center;'>Solar Employment Growth by Sector, 2010-2016</p>");
        options.setTitle(title);

//        HISubtitle subtitle = new HISubtitle();
//        subtitle.setText("Source: thesolarfoundation.com");
//        options.setSubtitle(subtitle);

        HIXAxis xAxis = new HIXAxis();
        xAxis.setAllowDecimals(false);
        HILabels hiLabels = new HILabels();
        HIFunction hiFunction = new HIFunction
                ("function () { return this.value; /*clean, unformatted number for year*/ }");
        hiLabels.setFormatter(hiFunction);
        hiLabels.setStyle(hicssObject);
        xAxis.setLabels(hiLabels);
        options.setXAxis(new ArrayList<HIXAxis>() {{
            add(xAxis);
        }});

        HIYAxis yaxis = new HIYAxis();
        HITitle title1 = new HITitle();
        HILabels labels = new HILabels();
        title1.setText("<p style='color: #ffffff; '>Number of Employees</p>");
        labels.setStyle(hicssObject);
        yaxis.setLabels(labels);
        yaxis.setTitle(title1);
        options.setYAxis(new ArrayList<>(Collections.singletonList(yaxis)));

        HILegend legend = new HILegend();
        legend.setLayout("vertical");
        legend.setAlign("right");
        legend.setVerticalAlign("middle");
        legend.setItemStyle(hicssObject);
        options.setLegend(legend);

        HIPlotOptions plotoptions = new HIPlotOptions();
        HISeries hiSeries = new HISeries();
        hiSeries.setPointStart(2010);
        HILabel hiLabel = new HILabel();
        hiSeries.setLabel(hiLabel);
        hiLabel.setConnectorAllowed(false);
        plotoptions.setSeries(hiSeries);

        HIExporting exporting = new HIExporting();
        exporting.setEnabled(false);
        options.setExporting(exporting);

        HILine line1 = new HILine();
        line1.setName("Installation");
        line1.setData(new ArrayList<>(Arrays.asList(43934, 52503, 57177, 69658, 97031, 119931, 137133, 154175)));

        /*HILine line2 = new HILine();
        line2.setName("Manufacturing");
        line2.setData(new ArrayList<>(Arrays.asList(24916, 24064, 29742, 29851, 32490, 30282, 38121, 404340)));

        HILine line3 = new HILine();
        line3.setName("Sales & Distribution");
        line3.setData(new ArrayList<>(Arrays.asList(11744, 17722, 16005, 19771, 20185, 24377, 32147, 39387)));

        HILine line4 = new HILine();
        line4.setName("Project Development");
        line4.setData(new ArrayList<>(Arrays.asList(null, null, 7988, 12169, 15112, 22452, 34400, 34227)));

        HILine line5 = new HILine();
        line5.setName("Other");
        line5.setData(new ArrayList<>(Arrays.asList(12908, 5948, 8105, 11248, 8989, 11816, 18274, 18111)));*/

        HIResponsive responsive = new HIResponsive();

        HIRules rules1 = new HIRules();
        HICondition hiCondition = new HICondition();
        hiCondition.setMaxWidth(500);
        rules1.setCondition(hiCondition);
        HashMap<String, HashMap> chartLegend = new HashMap<>();
        HashMap<String, String> legendOptions = new HashMap<>();
        legendOptions.put("layout", "horizontal");
        legendOptions.put("align", "center");
        legendOptions.put("verticalAlign", "bottom");
        chartLegend.put("legend", legendOptions);
        rules1.setChartOptions(chartLegend);
        responsive.setRules(new ArrayList<>(Collections.singletonList(rules1)));
        options.setResponsive(responsive);

        options.setSeries(new ArrayList<>(Arrays.asList(line1/*, line2, line3, line4, line5*/)));

        chartViewAchiGoals.setOptions(options);

        /*HIChart chart = new HIChart();
        chart.setType("column");
        options.setChart(chart);

        HITitle title = new HITitle();
        title.setText("Demo chart");

        options.setTitle(title);

        HIColumn series = new HIColumn();
        series.setData(new ArrayList<>(Arrays.asList(49.9, 71.5, 106.4, 129.2, 144, 176, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4)));
        options.setSeries(new ArrayList<HISeries>(Collections.singletonList(series)));

        chartView.setOptions(options);*/
    }


    private void setHeaderView() {
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        img_topbar_menu.setVisibility(View.VISIBLE);
    }
}
