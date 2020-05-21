package com.vaadin.demo.dashboard.view;

import com.vaadin.demo.dashboard.view.dashboard.DashboardView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public enum DashboardViewType {
    DASHBOARD("dashboard", DashboardView.class, VaadinIcon.HOME.create(), true);
//    SALES("sales", SalesView.class, VaadinIcon.BAR_CHART.create(), false),
//    TRANSACTIONS("transactions", TransactionsView.class, VaadinIcon.TABLE.create(), false),
//    REPORTS("reports", ReportsView.class, VaadinIcon.FILE_TEXT.create(), true),
//    SCHEDULE("schedule", ScheduleView.class, VaadinIcon.CALENDAR.create(), false);

    private final String viewName;
    private final Class viewClass;
    private final Icon icon;
    private final boolean stateful;

    private DashboardViewType(final String viewName,
                              final Class viewClass, final Icon icon,
                              final boolean stateful) {
        this.viewName = viewName;
        this.viewClass = viewClass;
        this.icon = icon;
        this.stateful = stateful;
    }

    public boolean isStateful() {
        return stateful;
    }

    public String getViewName() {
        return viewName;
    }

    public Class getViewClass() {
        return viewClass;
    }

    public Icon getIcon() {
        return icon;
    }

    public static DashboardViewType getByViewName(final String viewName) {
        DashboardViewType result = null;
        for (DashboardViewType viewType : values()) {
            if (viewType.getViewName().equals(viewName)) {
                result = viewType;
                break;
            }
        }
        return result;
    }

}
