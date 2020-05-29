package com.vaadin.demo.dashboard.view.reports;

import com.vaadin.demo.dashboard.view.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.internal.StateNode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Route(value = "reports", layout = MainLayout.class)
@PageTitle("Reports | QuickTickets")
@CssImport("./styles/reports-view.css")
public class ReportsView extends Div {

    public ReportsView() {
        ReportsViewComponent viewComponent = VaadinSession.getCurrent().getAttribute(ReportsViewComponent.class);
        if (viewComponent == null) {
            viewComponent = new ReportsViewComponent();
            VaadinSession.getCurrent().setAttribute(ReportsViewComponent.class, viewComponent);
        } else {
            viewComponent.getElement().removeFromTree();
        }
        setSizeFull();
        add(viewComponent);
    }
}
