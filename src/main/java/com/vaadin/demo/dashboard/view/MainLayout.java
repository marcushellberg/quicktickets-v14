package com.vaadin.demo.dashboard.view;

import com.google.common.eventbus.Subscribe;
import com.vaadin.demo.dashboard.AppContext;
import com.vaadin.demo.dashboard.domain.User;
import com.vaadin.demo.dashboard.data.dummy.DummyDataProvider;
import com.vaadin.demo.dashboard.event.DashboardEvent;
import com.vaadin.demo.dashboard.event.DashboardEventBus;
import com.vaadin.demo.dashboard.view.LoginView;
import com.vaadin.demo.dashboard.view.dashboard.DashboardView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;

@JsModule("@vaadin/vaadin-lumo-styles/presets/compact.js")
@CssImport("./styles/shared-styles.css")
public class MainLayout extends AppLayout {

    public MainLayout() {
        DashboardEventBus.register(this);

        addToDrawer(new DashboardMenu());
    }

    @Subscribe
    public void userLoggedOut(final DashboardEvent.UserLoggedOutEvent event) {
        // When the user logs out, current VaadinSession gets closed and the
        // page gets reloaded on the login screen. Do notice the this doesn't
        // invalidate the current HttpSession.
        VaadinSession.getCurrent().close();
        UI.getCurrent().getPage().reload();
    }
}
