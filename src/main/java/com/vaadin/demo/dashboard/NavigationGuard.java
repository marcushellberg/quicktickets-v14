package com.vaadin.demo.dashboard;

import com.vaadin.demo.dashboard.view.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

public class NavigationGuard implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent ->
            uiEvent.getUI().addBeforeEnterListener(this::beforeEnter));
    }

    /**
     * Reroutes the user if they're not authorized to access the view.
     *
     * @param event before navigation event with event details
     */
    private void beforeEnter(BeforeEnterEvent event) {
        if (!LoginView.class.equals(event.getNavigationTarget())
            && AppContext.getUser() == null) {
            event.rerouteTo(LoginView.class);
        }
    }
}