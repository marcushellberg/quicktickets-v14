package com.vaadin.demo.dashboard.component;


import com.vaadin.demo.dashboard.data.dummy.DummyDataGenerator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.charts.model.style.Color;
import com.vaadin.flow.component.charts.model.style.SolidColor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("serial")
public class SparklineChart extends VerticalLayout {

    public SparklineChart(final String name, final String unit,
                          final String prefix, final Color color, final int howManyPoints,
                          final int min, final int max) {
        setSizeUndefined();
        addClassName("spark");
        setMargin(false);
        setSpacing(false);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        int[] values = DummyDataGenerator.randomSparklineValues(howManyPoints,
                min, max);

        Span current = new Span(prefix + values[values.length - 1] + unit);
        current.addClassName("current");
        add(current);

        Span title = new Span(name);
        title.addClassName("title");
        title.setSizeUndefined();
        add(title);

        add(buildSparkline(values, color));

        List<Integer> vals = Arrays.asList(ArrayUtils.toObject(values));
        Html highLow = new Html(
                "<div>High <b>" + java.util.Collections.max(vals)
                        + "</b> &nbsp;&nbsp;&nbsp; Low <b>"
                        + java.util.Collections.min(vals) + "</b></div>");
        add(highLow);

    }

    private Component buildSparkline(final int[] values, final Color color) {
        Chart spark = new Chart();
        spark.getConfiguration().setTitle("");
        spark.getConfiguration().getChart().setType(ChartType.LINE);
        spark.getConfiguration().getChart().setAnimation(false);
        spark.setWidth("120px");
        spark.setHeight("40px");

        DataSeries series = new DataSeries();
        for (int value : values) {
            DataSeriesItem item = new DataSeriesItem("", value);
            series.add(item);
        }
        spark.getConfiguration().setSeries(series);
        spark.getConfiguration().getTooltip().setEnabled(false);

        Configuration conf = series.getConfiguration();
        Legend legend = new Legend();
        legend.setEnabled(false);
        conf.setLegend(legend);

        Credits c = new Credits("");
        spark.getConfiguration().setCredits(c);

        PlotOptionsLine opts = new PlotOptionsLine();
        opts.setAllowPointSelect(false);
        opts.setDataLabels(new DataLabels(false));
        opts.setShadow(false);
        opts.setMarker(new Marker(false));
        opts.setEnableMouseTracking(false);
        opts.setAnimation(false);
        spark.getConfiguration().setPlotOptions(opts);

        XAxis xAxis = spark.getConfiguration().getxAxis();
        YAxis yAxis = spark.getConfiguration().getyAxis();


        xAxis.setLabels(new Labels(false));

        yAxis.setTitle(new AxisTitle(""));
        yAxis.setLabels(new Labels(false));

        return spark;
    }
}
