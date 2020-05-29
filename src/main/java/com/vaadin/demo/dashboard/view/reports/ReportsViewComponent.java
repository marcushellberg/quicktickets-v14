package com.vaadin.demo.dashboard.view.reports;

import com.google.common.eventbus.Subscribe;
import com.vaadin.demo.dashboard.component.TabSheet;
import com.vaadin.demo.dashboard.event.DashboardEvent;
import com.vaadin.demo.dashboard.event.DashboardEventBus;
import com.vaadin.demo.dashboard.view.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ReportsViewComponent extends TabSheet implements
    ReportEditor.ReportEditorListener {



    public ReportsViewComponent() {
        setSizeFull();
        addClassName("reports");
        DashboardEventBus.register(this);
        addTab(buildDrafts(), "All Drafts", false);
    }


    private Component buildDrafts() {
        final VerticalLayout allDrafts = new VerticalLayout();
        allDrafts.setSizeFull();
        allDrafts.setMargin(false);
        allDrafts.setSpacing(false);

        VerticalLayout titleAndDrafts = new VerticalLayout();
        titleAndDrafts.setSizeUndefined();
        titleAndDrafts.setMargin(false);
        titleAndDrafts.addClassName("drafts");
        allDrafts.add(titleAndDrafts);
        allDrafts.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        allDrafts.setJustifyContentMode(JustifyContentMode.CENTER);

        H1 draftsTitle = new H1("Drafts");
        titleAndDrafts.add(draftsTitle);
        titleAndDrafts.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        titleAndDrafts.add(buildDraftsList());

        return allDrafts;
    }

    private Component buildDraftsList() {
        HorizontalLayout drafts = new HorizontalLayout();
        drafts.setDefaultVerticalComponentAlignment(Alignment.START);

        drafts.add(buildDraftThumb());
        drafts.add(buildCreateBox());

        return drafts;
    }

    private Component buildDraftThumb() {
        Div draftThumb = new Div();

        draftThumb.addClassName("draft-thumb");
        draftThumb.setWidth(null);
        Image draft = new Image("img/draft-report-thumb.png", "draft");
        draft.setWidth("160px");
        draft.setHeight("200px");
        draft.getElement().setAttribute("title", "Click to edit");
        draftThumb.add(draft);
        Html draftTitle = new Html(
            "<div>Monthly revenue<br><span>Last modified 1 day ago</span></div>");
        draftThumb.add(draftTitle);

        final Button delete = new Button("×");
        delete.getElement().setAttribute("title", "Delete draft");
        delete.addClassName("delete-button");
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        delete.addClickListener(click -> Notification.show("Not implemented in this demo"));
        draftThumb.add(delete);


        draftThumb.addClickListener(event -> {
            if (event.getButton() == 0) {
                addReport(ReportType.MONTHLY, null);
            }
        });

        return draftThumb;
    }

    private Component buildCreateBox() {
        VerticalLayout createBox = new VerticalLayout();
        createBox.setWidth("160px");
        createBox.setHeight("200px");
        createBox.addClassName("create");
        createBox.setMargin(false);
        createBox.setSpacing(false);

        Button create = new Button("Create New");
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        create.addClickListener(click -> addReport(ReportType.EMPTY, null));

        createBox.add(create);
        createBox.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        createBox.setJustifyContentMode(JustifyContentMode.CENTER);
        return createBox;
    }

    public void addReport(final ReportType reportType, final Object prefillData) {
        ReportEditor reportEditor = new ReportEditor(this);


        if (reportType == ReportType.MONTHLY) {
            reportEditor.setTitle("Monthly revenue");
            reportEditor.addWidget(ReportEditor.PaletteItemType.CHART, null);
            reportEditor.addWidget(ReportEditor.PaletteItemType.TABLE, null);
        } else if (reportType == ReportType.EMPTY) {
            DateFormat df = new SimpleDateFormat("M/dd/yyyy");
            reportEditor.setTitle("Unnamed Report – " + (df.format(new Date()))
                + " (" + getComponentCount() + ")");
        } else if (reportType == ReportType.TRANSACTIONS) {
            reportEditor
                .setTitle("Generated report from selected transactions");
            reportEditor.addWidget(ReportEditor.PaletteItemType.TEXT, "");
            reportEditor.addWidget(ReportEditor.PaletteItemType.TRANSACTIONS, prefillData);
        }

        addTab(reportEditor, reportEditor.getTitle(), true);

        DashboardEventBus.post(new DashboardEvent.ReportsCountUpdatedEvent(
            getComponentCount() - 1));
        setSelectedTab(getComponentCount() - 1);
    }

    @Subscribe
    public void createTransactionReport(final DashboardEvent.TransactionReportEvent event) {
        addReport(ReportType.TRANSACTIONS, event.getTransactions());
    }

    @Override
    protected void tabCloseRequested(Tab tab) {
        Span message = new Span(
            "You have not saved this report. Do you want to save or discard any changes you've made to this report?");
        message.setWidth("25em");

        final Dialog confirmDialog = new Dialog();
        confirmDialog.setCloseOnEsc(false);
        confirmDialog.setModal(true);
        confirmDialog.setResizable(false);

        VerticalLayout root = new VerticalLayout();
        root.add(new H4("Unsaved Changes"));
        confirmDialog.add(root);

        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidth("100%");

        root.add(message, footer);

        Button ok = new Button("Save", click -> {
            confirmDialog.close();
            removeTab(tab);
            DashboardEventBus.post(new DashboardEvent.ReportsCountUpdatedEvent(
                getComponentCount() - 1));
            Notification
                .show("The report was saved as a draft. Actually, the report was just closed and deleted forever. As this is only a demo, it doesn't persist any data.");
        });
        ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button discard = new Button("Discard Changes", click ->{
            confirmDialog.close();
            removeTab(tab);
            DashboardEventBus.post(new DashboardEvent.ReportsCountUpdatedEvent(
                getComponentCount() - 1));
        });
        discard.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        Button cancel = new Button("Cancel", click -> confirmDialog.close());

        footer.add(discard, cancel, ok);
        footer.expand(discard);
        confirmDialog.open();
    }

    public void titleChanged(final String newTitle, final ReportEditor editor) {
        getTab(editor).ifPresent(tab -> tab.setLabel(newTitle));
    }

    public enum ReportType {
        MONTHLY, EMPTY, TRANSACTIONS
    }
}
