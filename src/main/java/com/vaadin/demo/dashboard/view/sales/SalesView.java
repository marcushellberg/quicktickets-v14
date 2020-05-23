package com.vaadin.demo.dashboard.view.sales;

import com.vaadin.demo.dashboard.view.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "sales", layout = MainLayout.class)
@PageTitle("Sales | QuickTickets")
public class SalesView extends Div {
}
