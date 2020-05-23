package com.vaadin.demo.dashboard.view.dashboard;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

/**
 * Simple name editor Window.
 */
@SuppressWarnings("serial")
public class DashboardEdit extends Dialog {

    private final TextField nameField = new TextField("Name");
    private final DashboardEditListener listener;

    public DashboardEdit(final DashboardEditListener listener,
                         final String currentName) {
        this.listener = listener;
        setModal(true);
        setCloseOnOutsideClick(true);
        setResizable(false);
        setWidth("300px");

        add(buildContent(currentName));
    }

    private Component buildContent(final String currentName) {
        VerticalLayout result = new VerticalLayout();
        result.addClassName("edit-dashboard");
        Span header = new Span("Edit Dashboard");
        header.getElement().getThemeList().add("font-size-l");

        nameField.setValue(currentName);
        nameField.focus();

        result.add(header, nameField, buildFooter());

        return result;
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidth("100%");

        Button cancel = new Button("Cancel");
        cancel.addClickListener(e -> this.close());
        cancel.addClickShortcut(Key.ESCAPE);

        Button save = new Button("Save");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(e -> {
            listener.dashboardNameEdited(nameField.getValue());
            close();
        });
        save.addClickShortcut(Key.ENTER);

        footer.add(cancel, save);
        footer.expand(cancel);
        footer.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.START);
        return footer;
    }

    public interface DashboardEditListener {
        void dashboardNameEdited(String name);
    }
}
