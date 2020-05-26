package com.vaadin.demo.dashboard.component;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;

@SuppressWarnings("serial")
public class InlineTextEditor extends Div {

    private final Component editor;
    private final Component readOnly;
    private  Html text;

    public InlineTextEditor(final String initialValue) {
        setWidth("100%");
        addClassName("inline-text-editor");

        if (initialValue != null) {
            editor = buildEditor(initialValue);
            readOnly = buildReadOnly(initialValue);
        } else {
            editor = buildEditor("Enter text here...");
            readOnly = buildReadOnly("Enter text here...");
        }

        add(editor);
    }

    private Component buildReadOnly(final String initialValue) {
        text = new Html("<div>"+initialValue+"</div>");

        Button editButton = new Button(VaadinIcon.EDIT.create());
        editButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        editButton.addClickListener(event -> {
            removeAll();
            add(editor);
        });

        Div result = new Div(text, editButton);
        result.addClassName("text-editor");
        result.setSizeFull();
        result.addClickListener(event -> {
//            if (event.getChildComponent() == text && event.isDoubleClick()) {
//                setCompositionRoot(editor);
//            }
            removeAll();
            add(editor);
        });
        return result;
    }

    private Component buildEditor(final String initialValue) {
        final RichTextEditor rta = new RichTextEditor(initialValue);
        rta.addValueChangeListener(event -> {

            text = new Html("<div>"+event.getValue()+"</div>");

        });
        rta.setWidth("100%");

        Button save = new Button("Save");
        save.getElement().setAttribute("title","Edit");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            removeAll();
            add(readOnly);
        });

        Div result = new Div(rta, save);
        result.addClassName("edit");
        result.setSizeFull();
        return result;
    }

}
