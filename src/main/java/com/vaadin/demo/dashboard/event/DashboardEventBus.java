package com.vaadin.demo.dashboard.event;


import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;

/**
 * A simple wrapper for Guava event bus. Defines static convenience methods for
 * relevant actions.
 */
public class DashboardEventBus implements SubscriberExceptionHandler {

    public static void post(final Object event) {
        getEventBus().post(event);
    }

    public static void register(final Object object) {
        getEventBus().register(object);
    }

    public static void unregister(final Object object) {
        getEventBus().unregister(object);
    }

    private static EventBus getEventBus() {
        UI ui = UI.getCurrent();
        if(ComponentUtil.getData(ui, EventBus.class)==null){
            ComponentUtil.setData(ui, EventBus.class, new EventBus());
        }

        return ComponentUtil.getData(ui, EventBus.class);
    }

    @Override
    public final void handleException(final Throwable exception,
            final SubscriberExceptionContext context) {
        exception.printStackTrace();
    }
}
