package com.vaadin.demo.dashboard.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.internal.ReflectTools;
import com.vaadin.flow.server.VaadinSession;

import java.lang.reflect.ParameterizedType;

public class StatefulView<T extends Component> extends Div {

    public StatefulView() {
        add(initContent());
    }

    @SuppressWarnings("unchecked")
    protected T initContent() {
        Class<T> viewClass = (Class<T>) ((ParameterizedType) getClass()
            .getGenericSuperclass()).getActualTypeArguments()[0];
        T viewComponent =  VaadinSession.getCurrent().getAttribute(viewClass);
        if (viewComponent == null) {
            viewComponent = ReflectTools.createInstance(viewClass);
            VaadinSession.getCurrent().setAttribute(viewClass, viewComponent);
        } else {
            // Remove the view component from the previous UI
            viewComponent.getElement().removeFromTree();
        }
        return viewComponent;
    }
}
