package com.vaadin.demo.dashboard.component;


import com.vaadin.demo.dashboard.domain.Movie;
import com.vaadin.demo.dashboard.event.DashboardEventBus;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public final class MovieDetailsWindow extends Dialog {

    private final Paragraph synopsis = new Paragraph();

    private MovieDetailsWindow(final Movie movie, final Date startTime,
                               final Date endTime) {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("moviedetailswindow");
        add(layout);

        layout.add(new H2(movie.getTitle()));
        setCloseOnEsc(true);
        setResizable(false);

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        layout.add(content);
        content.setMargin(false);
        content.setSpacing(false);

        Scroller detailsWrapper = new Scroller(buildMovieDetails(movie, startTime,
                endTime));
        detailsWrapper.setSizeFull();
        detailsWrapper.addClassName("scroll-divider");
        content.add(detailsWrapper);
        content.expand(detailsWrapper);

        content.add(buildFooter());
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidth("100%");
        footer.setSpacing(false);

        Button ok = new Button("Close");
        ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        ok.addClickListener(e -> close());
        ok.focus();
        footer.add(ok);
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        return footer;
    }

    private Component buildMovieDetails(final Movie movie,
            final Date startTime, final Date endTime) {
        Div details = new Div();
        details.addClassName("detail");
        details.setWidth("100%");

        final Image coverImage = new Image(movie.getThumbUrl(), movie.getTitle());
        coverImage.addClassName("cover");
        details.add(coverImage);

        Component detailsForm = buildDetailsForm(movie, startTime, endTime);
        details.add(detailsForm);

        return details;
    }

    private Component buildDetailsForm(final Movie movie, final Date startTime,
                                       final Date endTime) {
        Div fields = new Div();
        fields.addClassName("fields");

        SimpleDateFormat df = new SimpleDateFormat();
        if (startTime != null) {

            df.applyPattern("dd-MM-yyyy");

            fields.add(new Span("Date"){{addClassName("label");}});
            fields.add(new Span(df.format(startTime)));

            df.applyPattern("hh:mm a");

            fields.add(new Span("Starts"){{addClassName("label");}});
            fields.add(new Span(df.format(startTime)));
        }

        if (endTime != null) {
            fields.add(new Span("Ends"){{addClassName("label");}});
            fields.add(new Span(df.format(endTime)));
        }

        fields.add(new Span("Duration"){{addClassName("label");}});
        fields.add(new Span(movie.getDuration() + " minutes"));

        fields.add(new Span("Synopsis"){{addClassName("label");}});
        synopsis.setText(movie.getSynopsis());
        updateSynopsis(movie, false);
        fields.add(new Paragraph(synopsis));

        final Button more = new Button("More…");
        more.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        fields.add(more);
        more.addClickListener(event -> {
                updateSynopsis(null, true);
                more.setVisible(false);
    });

        return fields;
    }

    private void updateSynopsis(final Movie m, final boolean expand) {
        String synopsisText = synopsis.getText();
        if (m != null) {
            synopsisText = m.getSynopsis();
        }
        if (!expand) {
            synopsisText = synopsisText.length() > 300 ? synopsisText
                    .substring(0, 300) + "…" : synopsisText;

        }
        synopsis.setText(synopsisText);
    }

    public static void open(final Movie movie, final Date startTime,
                            final Date endTime) {
        Dialog w = new MovieDetailsWindow(movie, startTime, endTime);
        w.open();
    }
}
