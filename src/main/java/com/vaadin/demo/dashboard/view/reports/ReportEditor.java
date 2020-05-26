package com.vaadin.demo.dashboard.view.reports;



import com.vaadin.demo.dashboard.component.InlineTextEditor;
import com.vaadin.demo.dashboard.component.TopSixTheatersChart;
import com.vaadin.demo.dashboard.component.TopTenMoviesTable;
import com.vaadin.demo.dashboard.component.TransactionsListing;
import com.vaadin.demo.dashboard.domain.Transaction;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.Collection;

@SuppressWarnings({ "serial", "unchecked" })
@CssImport("./styles/reports-editor.css")
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
        paletteLayout.setWidth("100%");
        paletteLayout.setJustifyContentMode(JustifyContentMode.CENTER);

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
        Icon icon = type.getIcon();
        icon.addClassName("icon");
        VerticalLayout item = new VerticalLayout(icon, new Span(type.getTitle()));
        item.addClassName("palette-item");
        item.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        item.setWidth(null);
        DragSource<VerticalLayout> ddWrap = DragSource.create(item);
        ddWrap.setDragData(type);
        return item;
    }

    public void addWidget(final PaletteItemType paletteItemType,
            final Object prefillData) {
        canvas.add(paletteItemType, prefillData);
    }

    static class SortableLayout extends Div implements DropTarget<VerticalLayout> {

        private final H2 placeholder;
        private Input titleLabel;

        public SortableLayout() {
            addClassName("canvas");
            setWidth(null);
            setActive(true);
            titleLabel = new Input();
            titleLabel.addClassName("title");

            placeholder = new H2("Drag items here");
            add(titleLabel);

            addDropListener(event -> {
                event.getDragSourceComponent().ifPresent(source -> {
                    if(!source.equals(this)) {
                        event.getDragData().ifPresent(type -> {
                            add((PaletteItemType) type, null);
                        });
                    }
                });

            });
        }

        public void setTitle(String title) {
            titleLabel.setValue(title);
        }

        public void add(final PaletteItemType paletteItemType,
                                 final Object prefillData) {
            if (placeholder.getParent().isPresent()) {
                remove(placeholder);
            }
            addComponentAtIndex(1,
                createComponentFromPaletteItem(
                    paletteItemType, prefillData));
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
            if(result != null) {
                ((HasSize) result).setHeight(null);
            }

            return result;
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