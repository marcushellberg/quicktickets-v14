package com.vaadin.demo.dashboard.view.reports;



import com.vaadin.demo.dashboard.component.InlineTextEditor;
import com.vaadin.demo.dashboard.component.TopSixTheatersChart;
import com.vaadin.demo.dashboard.component.TopTenMoviesTable;
import com.vaadin.demo.dashboard.component.TransactionsListing;
import com.vaadin.demo.dashboard.domain.Transaction;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;

@SuppressWarnings({ "serial", "unchecked" })
public final class ReportEditor extends VerticalLayout {

    private final ReportEditorListener listener;
    private final SortableLayout canvas;
    private String title;

    public ReportEditor(final ReportEditorListener listener) {
        this.listener = listener;
        setSizeFull();
        addClassName("editor");
        setMargin(false);
        setSpacing(false);

        Component palette = buildPalette();
        add(palette);
        setHorizontalComponentAlignment(Alignment.CENTER);

        canvas = new SortableLayout();
        canvas.setWidth("100%");
        canvas.addClassName("canvas");
        add(canvas);
        expand(canvas);
    }

    public void setTitle(final String title) {
        this.title = title;

        canvas.setTitle(title);
    }

    public String getTitle() {
        return title;
    }


    private Component buildPalette() {
        HorizontalLayout paletteLayout = new HorizontalLayout();
        paletteLayout.addClassName("palette");

        paletteLayout.add(buildPaletteItem(PaletteItemType.TEXT));
        paletteLayout.add(buildPaletteItem(PaletteItemType.TABLE));
        paletteLayout.add(buildPaletteItem(PaletteItemType.CHART));

//        paletteLayout.addClickListener(new LayoutClickListener() {
//            @Override
//            public void layoutClick(final LayoutClickEvent event) {
//                if (event.getChildComponent() != null) {
//                    PaletteItemType data = (PaletteItemType) ((DragAndDropWrapper) event
//                            .getChildComponent()).getData();
//                    addWidget(data, null);
//                }
//            }
//        });

        return paletteLayout;
    }

    private Component buildPaletteItem(final PaletteItemType type) {
        Html caption = new Html("<div>" + type.getIcon().getHtml() + type.getTitle() + "</div>");

        DragAndDropWrapper ddWrap = new DragAndDropWrapper(caption);
        ddWrap.setSizeUndefined();
        ddWrap.setDragStartMode(DragStartMode.WRAPPER);
        ddWrap.setData(type);
        return ddWrap;
    }

    public void addWidget(final PaletteItemType paletteItemType,
            final Object prefillData) {
        canvas.add(paletteItemType, prefillData);
    }



    public final class SortableLayout extends VerticalLayout {

        private final DropHandler dropHandler;
        private TextField titleLabel;
        private DragAndDropWrapper placeholder;

        public SortableLayout() {

            addClassName("canvas-layout");
            setMargin(false);
            setSpacing(false);

            titleLabel = new TextField();
            titleLabel.addClassName("title");
            SimpleDateFormat df = new SimpleDateFormat();
            df.applyPattern("M/dd/yyyy");

            titleLabel.addValueChangeListener(event -> {
                String t = titleLabel.getValue();
                if (t == null || t.equals("")) {
                    t = " ";
                }
                listener.titleChanged(t, ReportEditor.this);
            });
            add(titleLabel);

            dropHandler = new ReorderLayoutDropHandler();

            Span l = new Span("Drag items here");

            placeholder = new DragSource<Span>.create(l);
            placeholder.addClassName("placeholder");
            placeholder.setDropHandler(new DropHandler() {

                @Override
                public AcceptCriterion getAcceptCriterion() {
                    return AcceptAll.get();
                }

                @Override
                public void drop(final DragAndDropEvent event) {
                    Transferable transferable = event.getTransferable();
                    Component sourceComponent = transferable
                            .getSourceComponent();

                    if (sourceComponent != layout.getParent()) {
                        Object type = ((AbstractComponent) sourceComponent)
                                .getData();
                        add((PaletteItemType) type, null);
                    }
                }
            });
            add(placeholder);
        }

        public void setTitle(final String title) {
            titleLabel.setValue(title);
        }

        public void add(final PaletteItemType paletteItemType,
                final Object prefillData) {
            if (placeholder.getParent() != null) {
                remove(placeholder);
            }
            add(
                    new WrappedComponent(createComponentFromPaletteItem(
                            paletteItemType, prefillData)),
                    1);
        }

        private Component createComponentFromPaletteItem(
                final PaletteItemType type, final Object prefillData) {
            Component result = null;
            if (type == PaletteItemType.TEXT) {
                result = new InlineTextEditor(prefillData != null
                        ? String.valueOf(prefillData) : null);
            } else if (type == PaletteItemType.TABLE) {
                result = new TopTenMoviesTable();
            } else if (type == PaletteItemType.CHART) {
                result = new TopSixTheatersChart();
            } else if (type == PaletteItemType.TRANSACTIONS) {
                result = new TransactionsListing(
                        (Collection<Transaction>) prefillData);
            }

            return result;
        }

        private class WrappedComponent extends DragAndDropWrapper {

            public WrappedComponent(final Component content) {
                super(content);
                setDragStartMode(DragStartMode.WRAPPER);
            }

            @Override
            public DropHandler getDropHandler() {
                return dropHandler;
            }

        }

        private class ReorderLayoutDropHandler implements DropHandler {

            @Override
            public AcceptCriterion getAcceptCriterion() {
                // return new SourceIs(component)
                return AcceptAll.get();
            }

            @Override
            public void drop(final DragAndDropEvent dropEvent) {
                Transferable transferable = dropEvent.getTransferable();
                Component sourceComponent = transferable.getSourceComponent();

                TargetDetails dropTargetData = dropEvent.getTargetDetails();
                DropTarget target = dropTargetData.getTarget();

                if (sourceComponent.getParent() != layout) {
                    Object paletteItemType = ((AbstractComponent) sourceComponent)
                            .getData();

                    AbstractComponent c = new WrappedComponent(
                            createComponentFromPaletteItem(
                                    (PaletteItemType) paletteItemType, null));

                    int index = 0;
                    Iterator<Component> componentIterator = layout.iterator();
                    Component next = null;
                    while (next != target && componentIterator.hasNext()) {
                        next = componentIterator.next();
                        if (next != sourceComponent) {
                            index++;
                        }
                    }

                    if (dropTargetData.getData("verticalLocation")
                            .equals(VerticalDropLocation.TOP.toString())) {
                        index--;
                        if (index <= 0) {
                            index = 1;
                        }
                    }

                    addComponentAtIndex(index, c);
                }

                if (sourceComponent instanceof WrappedComponent) {
                    // find the location where to move the dragged component
                    boolean sourceWasAfterTarget = true;
                    int index = 0;
                    Iterator<Component> componentIterator = layout.iterator();
                    Component next = null;
                    while (next != target && componentIterator.hasNext()) {
                        next = componentIterator.next();
                        if (next != sourceComponent) {
                            index++;
                        } else {
                            sourceWasAfterTarget = false;
                        }
                    }
                    if (next == null || next != target) {
                        // component not found - if dragging from another layout
                        return;
                    }

                    // drop on top of target?
                    if (dropTargetData.getData("verticalLocation")
                            .equals(VerticalDropLocation.MIDDLE.toString())) {
                        if (sourceWasAfterTarget) {
                            index--;
                        }
                    }

                    // drop before the target?
                    else if (dropTargetData.getData("verticalLocation")
                            .equals(VerticalDropLocation.TOP.toString())) {
                        index--;
                        if (index <= 0) {
                            index = 1;
                        }
                    }

                    // move component within the layout
                    layout.remove(sourceComponent);
                    layout.addComponentAtIndex(index, sourceComponent);
                }
            }
        }

    }

    public interface ReportEditorListener {
        void titleChanged(String newTitle, ReportEditor editor);
    }

    public enum PaletteItemType {
        TEXT("Text Block", VaadinIcon.FONT.create()), TABLE("Top 10 Movies",
                VaadinIcon.TABLE.create()), CHART("Top 6 Revenue",
                        VaadinIcon.BAR_CHART.create()), TRANSACTIONS(
                                "Latest transactions", null);

        private final String title;
        private final Icon icon;

        PaletteItemType(final String title, final Icon icon) {
            this.title = title;
            this.icon = icon;
        }

        public String getTitle() {
            return title;
        }

        public Icon getIcon() {
            return icon;
        }

    }
}