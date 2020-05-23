package com.vaadin.demo.dashboard.view.transactions;

import com.vaadin.demo.dashboard.AppContext;
import com.vaadin.demo.dashboard.domain.Transaction;
import com.vaadin.demo.dashboard.event.DashboardEvent;
import com.vaadin.demo.dashboard.event.DashboardEventBus;
import com.vaadin.demo.dashboard.view.MainLayout;
import com.vaadin.demo.dashboard.view.dashboard.DashboardView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

@Route(value = "transactions", layout = MainLayout.class)
@PageTitle("Transactions | QuickTickets")
@CssImport("./styles/transactions-view.css")
public class TransactionsView extends VerticalLayout {
    private final Grid<Transaction> grid;
    private SingleSelect<Grid<Transaction>, Transaction> singleSelect;
    private Button createReport;
    private String filterValue = "";
    private static final DateFormat DATEFORMAT = new SimpleDateFormat(
        "MM/dd/yyyy hh:mm:ss a");
    private static final DecimalFormat DECIMALFORMAT = new DecimalFormat(
        "#.##");

    public TransactionsView() {
        setSizeFull();
        addClassName("transactions");
        setMargin(false);
        setSpacing(false);
        DashboardEventBus.register(this);

        add(buildToolbar());

        grid = buildGrid();
        singleSelect = grid.asSingleSelect();
        add(grid);
        expand(grid);

        addDetachListener(e -> DashboardEventBus.unregister(this));
    }

    private Component buildToolbar() {
        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("viewheader");
        header.setWidth("100%");
        header.setMargin(true);
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        H1 title = new H1("Latest Transactions");
        header.add(title);
        header.expand(title);

        createReport = buildCreateReport();
        HorizontalLayout tools = new HorizontalLayout(buildFilter(),
            createReport);
        tools.addClassName("toolbar");
        tools.setPadding(true);
        header.add(tools);

        return header;
    }

    private Button buildCreateReport() {
        final Button createReport = new Button("Create Report");
        createReport.getElement().setAttribute("title",
            "Create a new report from the selected transactions");
        createReport.addClickListener(event -> createNewReportFromSelection());
        createReport.setEnabled(false);
        return createReport;
    }

    private Component buildFilter() {
        final TextField filter = new TextField();

        // TODO use new filtering API
        filter.addValueChangeListener(event -> {

            Collection<Transaction> transactions = AppContext.getDataProvider()
                .getRecentTransactions(200).stream().filter(transaction -> {
                    filterValue = filter.getValue().trim().toLowerCase();
                    return passesFilter(transaction.getCountry())
                        || passesFilter(transaction.getTitle())
                        || passesFilter(transaction.getCity());
                }).collect(Collectors.toList());

            ListDataProvider<Transaction> dataProvider = DataProvider
                .ofCollection(transactions);
            dataProvider.addSortComparator(Comparator
                .comparing(Transaction::getTime).reversed()::compare);
            grid.setDataProvider(dataProvider);
        });

        filter.setPlaceholder("Filter");
        filter.setPrefixComponent(VaadinIcon.SEARCH.create());
        Shortcuts.addShortcutListener(filter, e -> {
            filter.setValue("");
        }, Key.ESCAPE);
        return filter;
    }

    private Grid<Transaction> buildGrid() {
        final Grid<Transaction> grid = new Grid<>();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setSizeFull();

        grid.addColumn(
            transaction -> DATEFORMAT.format(transaction.getTime())).setHeader("Time");

        grid.addColumn(Transaction::getCountry).setHeader("Country");
        grid.addColumn(Transaction::getCity).setHeader("City");
        grid.addColumn(Transaction::getTheater).setHeader("Theater");
        grid.addColumn(Transaction::getRoom).setHeader("Room");
        grid.addColumn(Transaction::getTitle).setHeader("Title");
        grid.addColumn(transaction -> String.valueOf(transaction.getSeats()))
            .setHeader("Seats");
        grid.addColumn(transaction -> "$"
            + DECIMALFORMAT.format(transaction.getPrice())).setHeader("Price");

        grid.setColumnReorderingAllowed(true);

        ListDataProvider<Transaction> dataProvider = DataProvider
            .ofCollection(AppContext.getDataProvider()
                .getRecentTransactions(200));
        dataProvider.addSortComparator(
            Comparator.comparing(Transaction::getTime).reversed()::compare);
        grid.setDataProvider(dataProvider);


        grid.addSelectionListener(
            event -> createReport.setEnabled(!singleSelect.isEmpty()));
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        return grid;
    }


    void createNewReportFromSelection() {
        if (!singleSelect.isEmpty()) {
            UI.getCurrent().navigate(DashboardView.class);
            DashboardEventBus.post(new DashboardEvent.TransactionReportEvent(
                Collections.singletonList(singleSelect.getValue())));
        }
    }

    private boolean passesFilter(String subject) {
        if (subject == null) {
            return false;
        }
        return subject.trim().toLowerCase().contains(filterValue);
    }
}
