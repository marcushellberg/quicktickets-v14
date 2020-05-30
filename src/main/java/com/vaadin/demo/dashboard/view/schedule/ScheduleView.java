package com.vaadin.demo.dashboard.view.schedule;

import com.google.common.eventbus.Subscribe;
import com.vaadin.demo.dashboard.AppContext;
import com.vaadin.demo.dashboard.component.MovieDetailsWindow;
import com.vaadin.demo.dashboard.component.TabSheet;
import com.vaadin.demo.dashboard.domain.Movie;
import com.vaadin.demo.dashboard.domain.Transaction;
import com.vaadin.demo.dashboard.event.DashboardEventBus;
import com.vaadin.demo.dashboard.view.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.stefan.fullcalendar.*;

import java.time.*;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

@Route(value = "schedule", layout = MainLayout.class)
@PageTitle("Schedule | QuickTickets")
@CssImport("./styles/schedule-view.css")
public final class ScheduleView extends VerticalLayout {

    private FullCalendar calendar;
    private final HorizontalLayout tray;

    public ScheduleView() {
        setSizeFull();
        addClassName("schedule");
        DashboardEventBus.register(this);

        TabSheet tabs = new TabSheet();
        tabs.setSizeFull();

        tabs.addTab(buildCalendarView(), "Calendar");
        tabs.addTab(buildCatalogView(), "Catalog");

        add(tabs);
        expand(tabs);

        tray = buildTray();
        add(tray);

        addDetachListener(e -> DashboardEventBus.unregister(this));
    }



    private Component buildCalendarView() {
        VerticalLayout calendarLayout = new VerticalLayout();
        calendarLayout.setSpacing(true);
        calendarLayout.setSizeFull();

        calendar = FullCalendarBuilder.create().build();
        calendar.setFirstDay(DayOfWeek.MONDAY);
        calendar.changeView(CalendarViewImpl.TIME_GRID_WEEK);
        calendar.setTimezone(Timezone.getSystem());
        calendar.setMinTime(LocalTime.of(11, 0));
        calendar.setMaxTime(LocalTime.of(23,0));

        Button previous = new Button("Previous", VaadinIcon.ANGLE_LEFT.create(), e -> calendar.previous());
        Button next = new Button("Next", VaadinIcon.ANGLE_RIGHT.create(), e -> calendar.next());
        HorizontalLayout toolbar = new HorizontalLayout(previous, next);
        toolbar.setWidth("100%");
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        calendarLayout.add(toolbar, calendar);
        populateCalendar();

        calendar.addEntryClickedListener(e -> {
            Entry entry = e.getEntry();
            AppContext
                .getDataProvider()
                .getMovies()
                .stream()
                .filter(m -> m.getTitle().equals(entry.getTitle()))
                .findFirst()
                .ifPresent(movie -> {
                    MovieDetailsWindow.open(movie,
                        Date.from(entry.getStart().atZone(calendar.getTimezone().getZoneId()).toInstant()),
                        Date.from(entry.getEnd().atZone(calendar.getTimezone().getZoneId()).toInstant()));
                });
        });
        calendar.addEntryDroppedListener(e -> setTrayVisible(true));
        calendar.addEntryResizedListener(e -> setTrayVisible(true));
        return calendarLayout;
    }

    private void populateCalendar() {
        calendar.removeAllEntries();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date lastWeek = cal.getTime();
        cal.add(Calendar.DATE, 14);
        Date nextWeek = cal.getTime();

        AppContext
            .getDataProvider()
            .getTransactionsBetween(lastWeek, nextWeek)
            .stream()
            .map(transaction -> {
                Movie movie = AppContext.getDataProvider().getMovie(transaction.getMovieId());
                Entry entry = new Entry();
                entry.setTitle(movie.getTitle());
                entry.setStart(transaction.getTime().toInstant().atZone(calendar.getTimezone().getZoneId()).toLocalDateTime(), calendar.getTimezone());
                entry.setEnd(entry.getStart(calendar.getTimezone()).plusMinutes(movie.getDuration()), calendar.getTimezone());
                return entry;
            }).forEach(calendar::addEntry);


    }

    private Component buildCatalogView() {
        Div catalog = new Div();
        catalog.addClassName("catalog");

        for (final Movie movie : AppContext.getDataProvider().getMovies()) {
            VerticalLayout frame = new VerticalLayout();
            frame.addClassName("frame");
            frame.setWidth(null);
            frame.setMargin(false);
            frame.setSpacing(false);

            Image poster = new Image(movie.getThumbUrl(), movie.getTitle());
            poster.setWidth("100px");
            poster.setHeight("145px");
            frame.add(poster);

            Span titleLabel = new Span(movie.getTitle());
            titleLabel.setWidth("120px");
            frame.add(titleLabel);

            frame.addClickListener(e ->
                        MovieDetailsWindow.open(movie, null, null));
            catalog.add(frame);
        }
        return catalog;
    }

    private HorizontalLayout buildTray() {
        final HorizontalLayout tray = new HorizontalLayout();
        tray.setWidth("100%");
        tray.addClassName("tray");
        tray.setMargin(true);

        Paragraph warning = new Paragraph(
            "You have unsaved changes made to the schedule");
        warning.addClassName("warning");
        warning.addClassName("icon-attention");
        tray.add(warning);
        tray.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        tray.expand(warning);

        Button confirm = new Button("Confirm");
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        confirm.addClickListener(click -> setTrayVisible(false));
        tray.add(confirm);
        tray.setAlignSelf(FlexComponent.Alignment.CENTER, confirm);

        Button discard = new Button("Discard");
        discard.addClickListener(click -> setTrayVisible(false));
        discard.addClickListener(click -> populateCalendar());
        tray.add(discard);
        tray.setAlignSelf(FlexComponent.Alignment.CENTER, discard);
        return tray;
    }

    private void setTrayVisible(final boolean visible) {
        final String styleReveal = "animate-reveal";
        if (visible) {
            tray.addClassName(styleReveal);
        } else {
            tray.removeClassName(styleReveal);
        }
    }

}
