package com.vaadin.demo.dashboard.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class DashboardHeader extends HorizontalLayout {
    public DashboardHeader() {
        addClassName("dashboard-header");
        setDefaultVerticalComponentAlignment(Alignment.CENTER);
        getElement().getThemeList().add("dark");
        Html appName = new Html("<div class='app-name'>QuickTickets <strong>Dashboard</strong></div>");
        add(new DrawerToggle(), appName);
        expand(appName);
    }
}
