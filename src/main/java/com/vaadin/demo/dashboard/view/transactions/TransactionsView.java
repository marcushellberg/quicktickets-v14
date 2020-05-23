package com.vaadin.demo.dashboard.view.transactions;

import com.vaadin.demo.dashboard.view.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "transactions", layout = MainLayout.class)
@PageTitle("Transactions | QuickTickets")
public class TransactionsView extends Div {
    public TransactionsView() {
        setText("Transactions");
    }
}
