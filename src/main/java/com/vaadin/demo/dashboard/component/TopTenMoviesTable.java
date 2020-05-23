package com.vaadin.demo.dashboard.component;


import com.vaadin.demo.dashboard.AppContext;
import com.vaadin.demo.dashboard.domain.MovieRevenue;
import com.vaadin.flow.component.grid.Grid;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public final class TopTenMoviesTable extends Grid<MovieRevenue> {

    DecimalFormat format = new DecimalFormat("#.##");

    public TopTenMoviesTable() {
        setSizeFull();

        List<MovieRevenue> movieRevenues = new ArrayList<MovieRevenue>(
                AppContext.getDataProvider().getTotalMovieRevenues());
        movieRevenues.sort((o1, o2) -> o2.getRevenue().compareTo(o1.getRevenue()));

        setItems(movieRevenues.subList(0, 10));


        addColumn(MovieRevenue::getTitle).setKey("title");
        addColumn(mr -> "$" + format.format(mr.getRevenue()));
        getColumnByKey("title").setFlexGrow(2);
    }

}
