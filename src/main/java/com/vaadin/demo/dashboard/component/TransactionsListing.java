package com.vaadin.demo.dashboard.component;

import com.vaadin.demo.dashboard.domain.Transaction;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

@SuppressWarnings("serial")
public class TransactionsListing extends Div {

    private final DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

    public TransactionsListing(final Collection<Transaction> transactions) {
        add(new Html("<strong>Selected transactions</strong>"));

        if (transactions != null) {
            for (Transaction transaction : transactions) {
                Div transationLayout = new Div();
                transationLayout.addClassName("transaction");

                Html content = new Html("<div class='time'>"
                    + df.format((transaction.getTime()))
                        + "<br>" + transaction.getCity() + ", "
                        + transaction.getCountry()
                        + "</div>"
                );
                transationLayout.add(content);

                Span title = new Span(transaction.getTitle());
                title.addClassName("movie-title");
                transationLayout.add(title);

                Html seats = new Html("<div class='seats'>Seats: "
                        + transaction.getSeats()
                        + "<br>"
                        + "Revenue: $"
                        + new DecimalFormat("#.##").format(transaction
                                .getPrice())
                        + "</div>");
                transationLayout.add(seats);

                add(transationLayout);
            }

        }
    }

}
