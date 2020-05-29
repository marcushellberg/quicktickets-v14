package com.vaadin.demo.dashboard.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class TabSheet extends VerticalLayout {

    private final Tabs tabs;
    private final Div content;
    private final Map<Tab, Component> tabsToComponents = new HashMap<>();

    public TabSheet() {
        tabs = new Tabs();
        tabs.setWidth("100%");
        content = new Div();
        content.setSizeFull();
        content.getStyle().set("overflow-y", "scroll");
        add(tabs, content);
        expand(content);
        tabs.addSelectedChangeListener(e -> {
            content.getChildren().forEach(c -> c.setVisible(false));
            tabsToComponents.get(e.getSelectedTab()).setVisible(true);
        });
    }

    public void addTab(Component component, String caption, boolean closeable) {
        Tab tab = new Tab();
        HorizontalLayout tabContent = new HorizontalLayout(new Span(caption));
        tabContent.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        if(closeable){
            Button closeButton = new Button(VaadinIcon.CLOSE.create());
            closeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
            closeButton.addClickListener(e -> tabCloseRequested(tab));
            tabContent.add(closeButton);
        }

        content.add(component);
        tab.add(tabContent);
        tabsToComponents.put(tab, component);
        tabs.add(tab);


        // Automatically show first tab
        if (tabs.getSelectedTab() == null) {
            tabs.setSelectedTab(tab);
        }
    }

    public void removeTab(Tab tab){
        if (tabs.getSelectedTab().equals(tab)) {
            tabs.remove(tab);
            tabsToComponents.remove(tab);

            if (tabsToComponents.size() > 0) {
                tabs.setSelectedIndex(getComponentCount() -1);
            }
        } else {
            tabs.remove(tab);
            tabsToComponents.remove(tab);
        }
    }

    public void setSelectedTab(int index){
        tabs.setSelectedIndex(index);
    }

    public Optional<Tab> getTab(Component content) {
        return tabsToComponents
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().equals(content))
            .map(Map.Entry::getKey)
            .findFirst();
    }

    abstract protected void tabCloseRequested(Tab tab);

    @Override
    public int getComponentCount() {
        return tabsToComponents.size();
    }
}
