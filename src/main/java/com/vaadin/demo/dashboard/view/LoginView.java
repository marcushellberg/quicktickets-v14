package com.vaadin.demo.dashboard.view;

import com.vaadin.demo.dashboard.AppContext;
import com.vaadin.demo.dashboard.domain.User;
import com.vaadin.demo.dashboard.event.DashboardEvent.UserLoginRequestedEvent;
import com.vaadin.demo.dashboard.event.DashboardEventBus;
import com.vaadin.demo.dashboard.view.dashboard.DashboardView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route("login")
@CssImport("./styles/login-view.css")
@PageTitle("Login | QuickTickets")
public class LoginView extends VerticalLayout {

    public LoginView() {
        addClassName("login-view");
        setSizeFull();
        setMargin(false);
        setSpacing(false);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        Component loginForm = buildLoginForm();
        add(loginForm);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Notification notification = new Notification(new Html(
            "<div class='welcome-notification'>" +
                "<p><b>Welcome to Dashboard Demo</b></p>" +
                "<p>This application is not real, it only demonstrates an application built with the <a href=\"https://vaadin.com\">Vaadin framework</a>.</p> " +
                "<p>No username or password is required, just click the <b>Sign In</b> button to continue.</p>" +
                "</div>"
        ));
        notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        notification.setPosition(Notification.Position.BOTTOM_CENTER);
        notification.setDuration(20000);
        notification.open();
        addDetachListener(e -> notification.close());
    }

    private Component buildLoginForm() {
        final VerticalLayout loginPanel = new VerticalLayout();
        loginPanel.setSizeUndefined();
        loginPanel.setMargin(false);
        loginPanel.addClassName("login-panel");

        loginPanel.add(buildLabels());
        loginPanel.add(buildFields());
        loginPanel.add(new Checkbox("Remember me", true));
        return loginPanel;
    }

    private Component buildFields() {
        HorizontalLayout fields = new HorizontalLayout();
        fields.addClassName("fields");

        final TextField username = new TextField("Username");
        username.setPrefixComponent(VaadinIcon.USER.create());

        final PasswordField password = new PasswordField("Password");
        password.setPrefixComponent(VaadinIcon.LOCK.create());

        final Button signin = new Button("Sign In");
        signin.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        signin.addClickShortcut(Key.ENTER);
        signin.focus();

        fields.add(username, password, signin);
        fields.setAlignSelf(Alignment.END, signin);

        signin.addClickListener(click ->
            login(username.getValue(), password.getValue())
        );
        return fields;
    }

    private Component buildLabels() {
        Div labels = new Div();
        labels.addClassName("labels");

        labels.add(new H4("Welcome"));
        labels.add(new H3("QuickTickets Dashboard"));
        return labels;
    }

    private void login(String username, String password) {
        User user = AppContext.getDataProvider().authenticate(username,
            password);
        AppContext.setUser(user);
        UI.getCurrent().navigate(DashboardView.class);
    }

}
