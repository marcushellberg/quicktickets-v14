package com.vaadin.demo.dashboard.view.schedule;

import com.vaadin.demo.dashboard.view.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "schedule", layout = MainLayout.class)
@PageTitle("Schedule | QuickTickets")
public class ScheduleView extends Div {
    public ScheduleView() {
        setText("Schedule");
    }
}
