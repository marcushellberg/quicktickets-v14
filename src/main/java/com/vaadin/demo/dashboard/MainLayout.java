package com.vaadin.demo.dashboard;

import com.google.common.eventbus.Subscribe;
import com.vaadin.demo.dashboard.domain.User;
import com.vaadin.demo.dashboard.data.dummy.DummyDataProvider;
import com.vaadin.demo.dashboard.event.DashboardEvent;
import com.vaadin.demo.dashboard.event.DashboardEventBus;
import com.vaadin.demo.dashboard.view.LoginView;
import com.vaadin.demo.dashboard.view.dashboard.DashboardView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinSession;

public class MainLayout extends AppLayout implements BeforeEnterObserver {

    public MainLayout() {
        AppContext.setDashboardEventbus(new DashboardEventBus());
        AppContext.setDataProvider(new DummyDataProvider());
        DashboardEventBus.register(this);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (AppContext.getUser() == null) {
            beforeEnterEvent.rerouteTo(LoginView.class);
        }
    }

    @Subscribe
    public void userLoginRequested(final DashboardEvent.UserLoginRequestedEvent event) {
        User user = AppContext.getDataProvider().authenticate(event.getUserName(),
            event.getPassword());
        AppContext.setUser(user);
        UI.getCurrent().navigate(DashboardView.class);
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
