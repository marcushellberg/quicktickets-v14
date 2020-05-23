package com.vaadin.demo.dashboard.view.dashboard;

import com.google.common.eventbus.Subscribe;
import com.vaadin.demo.dashboard.AppContext;
import com.vaadin.demo.dashboard.component.SparklineChart;
import com.vaadin.demo.dashboard.component.TopGrossingMoviesChart;
import com.vaadin.demo.dashboard.component.TopSixTheatersChart;
import com.vaadin.demo.dashboard.component.TopTenMoviesTable;
import com.vaadin.demo.dashboard.data.dummy.DummyDataGenerator;
import com.vaadin.demo.dashboard.domain.DashboardNotification;
import com.vaadin.demo.dashboard.event.DashboardEvent;
import com.vaadin.demo.dashboard.event.DashboardEventBus;
import com.vaadin.demo.dashboard.view.MainLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Collection;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard | QuickTickets")
@CssImport("./styles/dashboard-view.css")
@CssImport(value = "./styles/notes-card.css", themeFor = "vaadin-text-area")
public class DashboardView extends Scroller implements
    DashboardEdit.DashboardEditListener {
    public static final String EDIT_ID = "dashboard-edit";
    public static final String TITLE_ID = "dashboard-title";

    private H1 titleLabel;
    private NotificationsButton notificationsButton;
    private Div dashboardPanels;
    private final VerticalLayout root;
    private Dialog notificationsWindow;

    public DashboardView() {
        setSizeFull();
        DashboardEventBus.register(this);
        addClassName("dashboard-view");

        root = new VerticalLayout();
        root.setSizeFull();
        root.setSpacing(false);
        root.addClassName("root");
        setContent(root);

        root.add(buildHeader());

        root.add(buildSparklines());

        Component content = buildContent();
        root.add(content);
        root.expand(content);
    }

    private Component buildSparklines() {
        Div sparks = new Div();
        sparks.addClassName("sparks");
        sparks.setWidth("100%");

        sparks.add(
            new SparklineChart("Traffic", "K", "",
                DummyDataGenerator.chartColors[0], 22, 20, 80),
            new SparklineChart("Revenue / Day", "M", "$",
                DummyDataGenerator.chartColors[2], 8, 89, 150),
            new SparklineChart("Checkout Time", "s", "",
                DummyDataGenerator.chartColors[3], 10, 30, 120),
            new SparklineChart("Theater Fill Rate", "%", "",
                DummyDataGenerator.chartColors[5], 50, 34, 100)
        );
        return sparks;
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("viewheader");
        header.setPadding(true);
        header.setWidth("100%");
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        titleLabel = new H1("Dashboard");
        titleLabel.setId(TITLE_ID);
        header.add(titleLabel);
        header.expand(titleLabel);

        notificationsButton = buildNotificationsButton();
        Component edit = buildEditButton();
        HorizontalLayout tools = new HorizontalLayout(notificationsButton, edit);
        tools.addClassName("toolbar");
        header.add(tools);

        return header;
    }

    private NotificationsButton buildNotificationsButton() {
        NotificationsButton result = new NotificationsButton();
        result.addClickListener(this::openNotificationsPopup);
        return result;
    }

    private Component buildEditButton() {
        Button result = new Button();
        result.setId(EDIT_ID);
        result.setIcon(VaadinIcon.EDIT.create());
        result.addClassName("icon-edit");
        result.addThemeVariants(ButtonVariant.LUMO_ICON);
        result.getElement().setAttribute("title", "Edit Dashboard");
        result.addClickListener(click -> {
            new DashboardEdit(DashboardView.this, titleLabel
                .getText()).open();
        });
        return result;
    }

    private Component buildContent() {
        dashboardPanels = new Div();
        dashboardPanels.addClassName("dashboard-panels");
        dashboardPanels.add(buildTopGrossingMovies());
        dashboardPanels.add(buildNotes());
        dashboardPanels.add(buildTop10TitlesByRevenue());
        dashboardPanels.add(buildPopularMovies());

        return dashboardPanels;
    }

    private Component buildTopGrossingMovies() {
        TopGrossingMoviesChart topGrossingMoviesChart = new TopGrossingMoviesChart();
        topGrossingMoviesChart.setSizeFull();
        return createContentWrapper(topGrossingMoviesChart, "Top Grossing Movies");
    }

    private Component buildNotes() {
        TextArea notes = new TextArea();
        notes.setValue("Remember to:\n· Zoom in and out in the Sales view\n· Filter the transactions and drag a set of them to the Reports tab\n· Create a new report\n· Change the schedule of the movie theater");
        notes.setSizeFull();
        return createContentWrapper(notes, "Notes");
    }

    private Component buildTop10TitlesByRevenue() {
        Div contentWrapper = createContentWrapper(new TopTenMoviesTable(), "Top 10 Titles by Revenue");
        contentWrapper.addClassName("top10-revenue");
        return contentWrapper;
    }

    private Component buildPopularMovies() {
        return createContentWrapper(new TopSixTheatersChart(), "Popular Movies");
    }

    private Div createContentWrapper(final Component content, String captionText) {
        final Div slot = new Div();
        slot.setWidth("100%");
        slot.addClassName("dashboard-panel-slot");

        Div card = new Div();
        card.setWidth("100%");
        card.addClassName("card");

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.addClassName("dashboard-panel-toolbar");
        toolbar.setWidth("100%");
        toolbar.setSpacing(false);

        H4 caption = new H4(captionText);

        Button expandButton = new Button(VaadinIcon.EXPAND.create());
        expandButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        expandButton.addClickListener(click -> {
            if (!slot.getClassName().contains("max")) {
                expandButton.setIcon(VaadinIcon.COMPRESS.create());
                toggleMaximized(slot, true);
            } else {
                slot.removeClassName("max");
                expandButton.setIcon(VaadinIcon.EXPAND.create());
                toggleMaximized(slot, false);
            }
        });

        Div header = new Div(caption, expandButton);
        header.addClassName("header");
        content.getElement().getClassList().add("content");
        card.add(header, content);
        slot.add(card);
        return slot;
    }

    private void openNotificationsPopup(final ClickEvent<Button> event) {
        VerticalLayout notificationsLayout = new VerticalLayout();

        H3 title = new H3("Notifications");
        notificationsLayout.add(title);

        Collection<DashboardNotification> notifications = AppContext
            .getDataProvider().getNotifications();
        DashboardEventBus.post(new DashboardEvent.NotificationsCountUpdatedEvent());

        for (DashboardNotification notification : notifications) {
            VerticalLayout notificationLayout = new VerticalLayout();
            notificationLayout.setMargin(false);
            notificationLayout.setSpacing(false);
            notificationLayout.addClassName("notification-item");

            Span titleLabel = new Span(notification.getFirstName() + " "
                + notification.getLastName() + " "
                + notification.getAction());
            titleLabel.addClassName("notification-title");

            Span timeLabel = new Span(notification.getPrettyTime());
            timeLabel.addClassName("notification-time");

            Span contentLabel = new Span(notification.getContent());
            contentLabel.addClassName("notification-content");

            notificationLayout.add(titleLabel, timeLabel,
                contentLabel);
            notificationsLayout.add(notificationLayout);
        }

        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidth("100%");
        footer.setSpacing(false);
        Button showAll = new Button("View All Notifications",
            e -> Notification.show("Not implemented in this demo"));
        footer.add(showAll);
        footer.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        notificationsLayout.add(footer);

        if (notificationsWindow == null) {
            notificationsWindow = new Dialog();
            notificationsWindow.setWidth("300px");
            notificationsWindow.setResizable(false);
            notificationsWindow.setDraggable(false);
            notificationsWindow.setCloseOnEsc(true);
            notificationsLayout.addClassName("notifications");
            notificationsWindow.add(notificationsLayout);
        }

        if (notificationsWindow.isOpened())  {
            notificationsWindow.close();
        } else {
            notificationsWindow.open();
        }
    }


    @Override
    public void dashboardNameEdited(final String name) {
        titleLabel.setText(name);
    }

    private void toggleMaximized(final Div panel, final boolean maximized) {
        root.getChildren().forEach(component -> component.setVisible(!maximized));
        dashboardPanels.setVisible(true);
        dashboardPanels.getChildren().forEach(component -> component.setVisible(!maximized));

        if (maximized) {
            panel.setVisible(true);
            panel.addClassName("max");
        } else {
            panel.removeClassName("max");
        }
    }

    public static final class NotificationsButton extends Button {
        private static final String STYLE_UNREAD = "unread";
        public static final String ID = "dashboard-notifications";

        public NotificationsButton() {
            setIcon(VaadinIcon.BELL.create());
            setId(ID);
            addClassName("notifications");
            addThemeVariants(ButtonVariant.LUMO_ICON);
            DashboardEventBus.register(this);
        }

        @Subscribe
        public void updateNotificationsCount(
            final DashboardEvent.NotificationsCountUpdatedEvent event) {
            setUnreadCount(AppContext.getDataProvider()
                .getUnreadNotificationsCount());
        }

        public void setUnreadCount(final int count) {
            setText(String.valueOf(count));

            String description = "Notifications";
            if (count > 0) {
                addClassName(STYLE_UNREAD);
                description += " (" + count + " unread)";
            } else {
                removeClassName(STYLE_UNREAD);
            }
            getElement().setAttribute("title", description);
        }
    }
}
