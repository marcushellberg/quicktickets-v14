package com.vaadin.demo.dashboard.view.reports;



import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

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
        VerticalLayout item = new VerticalLayout(type.getIcon(), new Span(type.getTitle()));

        DragSource<VerticalLayout> ddWrap = DragSource.create(item);
        ddWrap.setDragData(type);
        return item;
    }

    public void addWidget(final PaletteItemType paletteItemType,
            final Object prefillData) {
        canvas.add(paletteItemType, prefillData);
    }

    static class SortableLayout extends VerticalLayout implements DropTarget<VerticalLayout> {

        private TextField titleLabel;

        public SortableLayout() {
            titleLabel = new TextField();
        }

        public void setTitle(String title) {
            titleLabel.setValue(title);
        }

        public void add(PaletteItemType paletteItemType, Object prefillData) {

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