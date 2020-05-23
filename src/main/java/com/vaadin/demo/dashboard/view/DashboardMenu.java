package com.vaadin.demo.dashboard.view;

import com.vaadin.demo.dashboard.AppContext;
import com.vaadin.demo.dashboard.domain.User;
import com.vaadin.demo.dashboard.event.DashboardEventBus;
import com.vaadin.demo.dashboard.view.dashboard.DashboardView;
import com.vaadin.demo.dashboard.view.reports.ReportsView;
import com.vaadin.demo.dashboard.view.sales.SalesView;
import com.vaadin.demo.dashboard.view.schedule.ScheduleView;
import com.vaadin.demo.dashboard.view.transactions.TransactionsView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ElementUtil;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.Command;

@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
// Here, we add the style sheet to the global scope
@CssImport("./styles/sidebar.css")
@CssImport(value = "./styles/profile-menu.css", themeFor = "vaadin-menu-bar")
@CssImport(value = "./styles/profile-menu-bar-button.css", themeFor = "vaadin-menu-bar-button")
public final class DashboardMenu extends Div {


    public DashboardMenu() {
        add(new Html("<style include='lumo-badge'></style>"));
        addClassName("sidebar");
        getElement().getThemeList().add("dark");
        add(new Html("<div class='app-name'>QuickTickets <strong>Dashboard</strong></div>"));
        add(buildUserMenu());
        add(buildMenuItems());
    }

    private Component buildUserMenu() {
        MenuBar settings = new MenuBar();
        User user = AppContext.getUser();

        Image profileImage = new Image("img/profile-pic-300px.jpg", "profile");
        Span userName = new Span(user.getFirstName() + " " + user.getLastName());
        VerticalLayout profileInfo = new VerticalLayout(profileImage, userName);
        profileInfo.addClassName("profile");
        profileInfo.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);


        MenuItem settingsItem = settings.addItem(profileInfo);
        settingsItem.getSubMenu().addItem("Edit Profile", e -> {
        });
        settingsItem.getSubMenu().addItem("Preferences", e -> {
        });
        settingsItem.getSubMenu().addItem("Sign out", e -> {
        });
        return settings;
    }

    private Component buildMenuItems() {
        Div navLayout = new Div();
        navLayout.addClassName("navigation-items-layout");

        NavigationMenuItem dashboard = new NavigationMenuItem("Dashboard", VaadinIcon.HOME.create(), DashboardView.class);
        dashboard.setBadgeText("2");
        navLayout.add(
            dashboard,
            new NavigationMenuItem("Sales", VaadinIcon.BAR_CHART.create(), SalesView.class),
            new NavigationMenuItem("Transactions", VaadinIcon.TABLE.create(), TransactionsView.class),
            new NavigationMenuItem("Reports", VaadinIcon.FILE_TEXT.create(), ReportsView.class),
            new NavigationMenuItem("Schedule", VaadinIcon.CALENDAR.create(), ScheduleView.class)
        );

        return navLayout;
    }

    static class NavigationMenuItem extends Div {

        private final Span badge;

        public NavigationMenuItem(String caption, Icon icon, Class<? extends Component> target) {
            addClassName("menu-item");
            icon.addClassName("icon");

            RouterLink link = new RouterLink(caption, target);
            link.setHighlightCondition(HighlightConditions.sameLocation());

            badge = new Span();
            badge.getElement().getThemeList().add("badge");
            badge.addClassName("badge");
            badge.setVisible(false);

            add(icon, link, badge);

        }

        public void setBadgeText(String text){
            badge.setText(text);
            badge.setVisible(true);
        }
    }

}
