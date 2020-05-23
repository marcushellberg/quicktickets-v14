package com.vaadin.demo.dashboard.view.sales;

import com.vaadin.demo.dashboard.AppContext;
import com.vaadin.demo.dashboard.domain.Movie;
import com.vaadin.demo.dashboard.domain.MovieRevenue;
import com.vaadin.demo.dashboard.view.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Route(value = "sales", layout = MainLayout.class)
@PageTitle("Sales | QuickTickets")
public class SalesView extends VerticalLayout {
    private final Chart timeline;
    private ComboBox<Movie> movieSelect;
    private Collection<Movie> movies;

    public SalesView() {
        setSizeFull();
        addClassName("sales");
        setMargin(false);
        setSpacing(false);

        add(buildHeader());

        timeline = buildTimeline();
        add(timeline);
        expand(timeline);

        initMovieSelect();
        // Add first 4 by default
        List<Movie> subList = new ArrayList<Movie>(
            AppContext.getDataProvider().getMovies()).subList(0, 4);
        for (Movie m : subList) {
            addDataSet(m);
        }
    }

    private void initMovieSelect() {
        movies = new HashSet<>(AppContext.getDataProvider().getMovies());
        movieSelect.setItems(movies);
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("viewheader");

        H1 titleLabel = new H1("Revenue by Movie");
        header.add(titleLabel, buildToolbar());

        return header;
    }

    private Component buildToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.addClassName("toolbar");

        movieSelect = new ComboBox<>();
        movieSelect.setItemLabelGenerator(Movie::getTitle);
        movieSelect.addCustomValueSetListener(e ->{
           addDataSet(movieSelect.getValue());
        });

        final Button add = new Button("Add");
        add.setEnabled(false);
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Div group = new Div(movieSelect, add);
        toolbar.add(group);

        movieSelect.addValueChangeListener(
            event -> add.setEnabled(event.getValue() != null));

        final Button clear = new Button("Clear");
        clear.addClassName("clearbutton");
        clear.addClickListener(click -> {
            timeline.getConfiguration().setSeries(new ArrayList<>());
            timeline.drawChart();
            initMovieSelect();
            clear.setEnabled(false);
        });
        toolbar.add(clear);

        add.addClickListener(click -> {
            addDataSet(movieSelect.getValue());
            clear.setEnabled(true);
        });

        return toolbar;
    }

    private Chart buildTimeline() {
        Chart result = new Chart();
        result.setSizeFull();

        result.setTimeline(true);

        result.getConfiguration().getRangeSelector().setEnabled(false);

        Legend legend = result.getConfiguration().getLegend();
        legend.setAlign(HorizontalAlign.RIGHT);
        legend.setVerticalAlign(VerticalAlign.TOP);
        legend.setEnabled(true);
        return result;
    }

    private void addDataSet(final Movie movie) {
        movies.remove(movie);
        movieSelect.setValue(null);
        movieSelect.getDataProvider().refreshAll();

        Collection<MovieRevenue> revenues = AppContext.getDataProvider()
            .getDailyRevenuesByMovie(movie.getId());

        DataSeries movieSeries = new DataSeries();
        for (MovieRevenue revenue : revenues) {
            DataSeriesItem item = new DataSeriesItem();
            item.setX(revenue.getTimestamp());
            item.setY(revenue.getRevenue());
            movieSeries.add(item);
        }
        movieSeries.setName(movie.getTitle());
        timeline.getConfiguration().addSeries(movieSeries);
        timeline.drawChart();
    }
}
