package com.vaadin.demo.dashboard.view.dashboard;

import com.vaadin.demo.dashboard.view.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard | QuickTickets")
public class DashboardView extends Div {
    public DashboardView() {
        setText("DASHBORAT");
    }
}
