package com.vaadin.demo.dashboard.view.reports;

import com.vaadin.demo.dashboard.view.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "reports", layout = MainLayout.class)
@PageTitle("Reports | QuickTickets")
public class ReportsView extends Div {
    public ReportsView() {
        setText("reports");
    }
}
