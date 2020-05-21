package com.vaadin.demo.dashboard;

import com.vaadin.demo.dashboard.data.DataProvider;
import com.vaadin.demo.dashboard.domain.User;
import com.vaadin.demo.dashboard.event.DashboardEventBus;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;

public class AppContext {

    public static void setDashboardEventbus(DashboardEventBus eventBus) {
        saveToUI(DashboardEventBus.class, eventBus);
    }

    public static DashboardEventBus getDashboardEventbus(){
        return readFromUI(DashboardEventBus.class);
    }

    public static void setDataProvider(DataProvider dataProvider){
        saveToUI(DataProvider.class, dataProvider);
    }

    public static DataProvider getDataProvider(){
        return readFromUI(DataProvider.class);
    }

    public static void setUser(User user) {
        saveToUI(User.class, user);
    }

    public static User getUser(){
        return readFromUI(User.class);
    }

    private static <T> void saveToUI(Class<T> key, T value){
        ComponentUtil.setData(UI.getCurrent(), key, value);
    }

    private static <T> T readFromUI(Class<T> key){
        return ComponentUtil.getData(UI.getCurrent(), key);
    }

}
