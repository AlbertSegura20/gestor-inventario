package com.apec.poo.view;

import com.apec.poo.entities.Transaction;
import com.apec.poo.repository.TransactionRepository;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;


@ApplicationScoped
public class TransactionsGridView extends Composite<VerticalLayout> {

    private final TransactionRepository transactionRepository;
    private static final String FULL_WIDTH = "100%";
    private static final String MAX_WIDTH = "100%";
    private static final String WIDTH = "160px";
    private static final String MIN_CONTENT = "min-content";
    private final Grid<Transaction> transactionGrid;
    private static final String WIDTH_100 = "100px";
    private static final String WIDTH_130 = "130px";
    private static final String WIDTH_180 = "180px";
//    Icon printIcon = new Icon("vaadin", "print");
//    Icon editIcon = new Icon("vaadin", "edit");
//    Icon deleteIcon = new Icon("vaadin", "delete");
//    Icon cancelIcon = new Icon("vaadin", "close");
//    Icon saveIcon = new Icon("vaadin", "check");
//    Icon errorIcon = new Icon("vaadin", "error");
//    Icon successIcon = new Icon("vaadin", "check-circle");
//    Icon warningIcon = new Icon("vaadin", "exclamation-triangle");
//    Icon infoIcon = new Icon("vaadin", "info-circle");
//    Icon closeIcon = new Icon("vaadin", "close-circle");

    @Inject
    public TransactionsGridView(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        VerticalLayout mainLayout = createMainLayout();

        // Add a title
        mainLayout.add(new H3("Transaction Information"));
        // Add Grid
        transactionGrid = createTransactionGrid();
        mainLayout.add(transactionGrid);
        getContent().add(mainLayout);
        fillGridWithData();

    }


    private VerticalLayout createMainLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidth(FULL_WIDTH);
        mainLayout.setMaxWidth(MAX_WIDTH);
        mainLayout.setHeight(MIN_CONTENT);

        getContent().setWidth(FULL_WIDTH);
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);

        return mainLayout;
    }

    private Grid<Transaction> createTransactionGrid() {
        Grid<Transaction> grid = new Grid<>(Transaction.class);
        Editor<Transaction> editor = grid.getEditor();
        grid.removeAllColumns();

        // Client-related column
        grid.addColumn(this::formatClientName)
                .setHeader("Client name")
                .setWidth(WIDTH)
                .setFlexGrow(0);

        // Product-related columns
        grid.addColumn(transaction -> transaction.getProduct().getName())
                .setHeader("Product name")
                .setWidth(WIDTH)
                .setFlexGrow(0);
        grid.addColumn(transaction -> transaction.getProduct().getCode())
                .setHeader("Product code")
                .setWidth(WIDTH);
        grid.addColumn(transaction -> transaction.getProduct().getQuantity())
                .setHeader("Quantity")
                .setWidth(WIDTH_100)
                .setFlexGrow(0);
        grid.addColumn(transaction -> transaction.getProduct().getRegistryDate())
                .setHeader("Registration date")
                .setWidth(WIDTH)
                .setFlexGrow(0);
        grid.addColumn(transaction -> transaction.getProduct().getPrice())
                .setHeader("Price")
                .setWidth(WIDTH_100);

        // Transaction-related columns
        grid.addColumn(Transaction::getQuantityTransaction)
                .setHeader("Total quantity")
                .setWidth(WIDTH_130)
                .setFlexGrow(0);
        grid.addColumn(Transaction::getTotalPrice)
                .setHeader("Total price")
                .setWidth(WIDTH_100)
                .setFlexGrow(0);
        grid.addColumn(Transaction::getTransactionDate)
                .setHeader("Transaction date")
                .setWidth(WIDTH_180);

        // Edit column
        grid.addComponentColumn(transaction -> createEditButton(transaction, editor, grid))
                .setWidth(WIDTH)
                .setFlexGrow(0);

        grid.setWidthFull();
        return grid;
    }

    // Helper method to format client's full name
    private String formatClientName(Transaction transaction) {
        return transaction.getClient().getName() + " " + transaction.getClient().getLastName();
    }

    // Helper method to create edit buttons
    private Button createEditButton(Transaction transaction, Editor<Transaction> editor, Grid<Transaction> grid) {
        Button editButton = new Button(new Icon("vaadin", "print"));
        editButton.addClickListener(e -> {
            if (editor.isOpen()) {
                editor.cancel();
            }
            grid.getEditor().editItem(transaction);
        });
        return editButton;
    }




    private void fillGridWithData(){
        List<Transaction> transactions = transactionRepository.findAll().list();
        transactionGrid.setItems(transactions);
    }

}
