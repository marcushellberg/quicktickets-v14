package com.vaadin.demo.dashboard.view.reports;

import com.vaadin.demo.dashboard.component.StatefulView;
import com.vaadin.demo.dashboard.view.MainLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "reports", layout = MainLayout.class)
@PageTitle("Reports | QuickTickets")
@CssImport("./styles/reports-view.css")
public class ReportsView extends StatefulView<ReportsViewComponent> {

    public ReportsView() {
        setSizeFull();
    }
}
