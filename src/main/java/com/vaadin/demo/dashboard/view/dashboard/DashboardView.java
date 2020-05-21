package com.vaadin.demo.dashboard.view.dashboard;

import com.vaadin.demo.dashboard.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
public class DashboardView extends Div {
    public DashboardView() {
        setText("DASHBORAT");
    }
}
