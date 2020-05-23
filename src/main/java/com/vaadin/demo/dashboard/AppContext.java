package com.vaadin.demo.dashboard;

import com.vaadin.demo.dashboard.data.DataProvider;
import com.vaadin.demo.dashboard.data.dummy.DummyDataProvider;
import com.vaadin.demo.dashboard.domain.User;
import com.vaadin.demo.dashboard.event.DashboardEventBus;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

public class AppContext {

    public static void setDashboardEventbus(DashboardEventBus eventBus) {
        saveToUI(DashboardEventBus.class, eventBus);
    }

    public static DashboardEventBus getDashboardEventbus() {
        DashboardEventBus eventBus = readFromUI(DashboardEventBus.class);
        if (eventBus == null) {
            eventBus = new DashboardEventBus();
            setDashboardEventbus(eventBus);
        }
        return eventBus;
    }

    public static void setDataProvider(DataProvider dataProvider) {
        saveToUI(DataProvider.class, dataProvider);
    }

    public static DataProvider getDataProvider() {
        DataProvider dataProvider = readFromUI(DataProvider.class);
        if (dataProvider == null) {
            dataProvider = new DummyDataProvider();
            setDataProvider(dataProvider);
        }
        return dataProvider;
    }

    public static void setUser(User user) {
        VaadinSession.getCurrent().setAttribute(User.class, user);
    }

    public static User getUser() {
        return VaadinSession.getCurrent().getAttribute(User.class);
    }

    private static <T> void saveToUI(Class<T> key, T value) {
        ComponentUtil.setData(UI.getCurrent(), key, value);
    }

    private static <T> T readFromUI(Class<T> key) {
        return ComponentUtil.getData(UI.getCurrent(), key);
    }

}
