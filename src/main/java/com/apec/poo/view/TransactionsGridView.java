package com.apec.poo.view;

import com.apec.poo.entities.Transaction;
import com.apec.poo.repository.TransactionRepository;
import com.apec.poo.utils.CsvService;
import com.apec.poo.utils.PdfService;
import com.apec.poo.utils.Utils;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.server.StreamResource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;


@ApplicationScoped
public class TransactionsGridView extends Composite<VerticalLayout> {

    private final TransactionRepository transactionRepository;
    private static final String FULL_WIDTH = "100%";
    private static final String WIDTH = "160px";
    private static final String MIN_CONTENT = "min-content";
    private final Grid<Transaction> transactionGrid;
    private static final String WIDTH_100 = "100px";
    private static final String WIDTH_130 = "130px";
    private static final String WIDTH_180 = "180px";
    private static final String VAADIN = "vaadin";
    private static final String PRINT = "print";
    private final CsvService csvService;
    private final PdfService pdfService;
    private final Map<Long, Boolean> selectedRow = new HashMap<>();

    @Inject
    public TransactionsGridView(TransactionRepository transactionRepository, CsvService csvService, PdfService pdfService) {
        this.transactionRepository = transactionRepository;
        VerticalLayout mainLayout = createMainLayout();
        this.csvService = csvService;
        this.pdfService = pdfService;
        // Add a title
        mainLayout.add(createDivForTitleandFilter());
        // Add Grid
        transactionGrid = createTransactionGrid();
        mainLayout.add(transactionGrid, createDivForButtons());
        getContent().add(mainLayout);

    }


    private VerticalLayout createMainLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidth(FULL_WIDTH);
        mainLayout.setHeight(MIN_CONTENT);

        getContent().setWidth(FULL_WIDTH);
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setPadding(false);

        return mainLayout;
    }

    private HorizontalLayout createDivForButtons(){
        HorizontalLayout divLayout = new HorizontalLayout();
        divLayout.setWidthFull();
        divLayout.add(printSelectedTransactions(), printAllTransactions());
        return divLayout;
    }

    private HorizontalLayout createDivForTitleandFilter(){
        HorizontalLayout divLayout = new HorizontalLayout();
        divLayout.setWidthFull();
        divLayout.add(new H3("Transaction List"), createFilterField());
        divLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        return divLayout;
    }


    private TextField createFilterField() {
        TextField filterField = new TextField();
        filterField.setPlaceholder("Filter...");
        filterField.setClearButtonVisible(true);
        filterField.setPrefixComponent(new Icon(VAADIN, "search"));
        filterField.addValueChangeListener(e -> filterTransactions(e.getValue()));
        return filterField;
    }

    private Grid<Transaction> createTransactionGrid() {
        Grid<Transaction> grid = new Grid<>(Transaction.class);
        grid.getEditor();
        grid.removeAllColumns();

        grid.addColumn(new ComponentRenderer<>(item -> {
            Checkbox checkbox = new Checkbox();
            checkbox.addValueChangeListener(event -> selectedRow.put(item.getId(), event.getValue()));
            return checkbox;
        }));

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
                .setHeader("Quantity available")
                .setWidth("150px")
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

        grid.addComponentColumn(this::createPrintButton)
                .setWidth(WIDTH_100)
                .setFlexGrow(0);

        grid.setWidthFull();
        return grid;
    }

    // Helper method to format client's full name
    private String formatClientName(Transaction transaction) {
        return transaction.getClient().getName() + " " + transaction.getClient().getLastName();
    }

    // Helper method to create print button
    private Button createPrintButton(Transaction transaction) {
        Button printButton = new Button(new Icon(VAADIN, PRINT));
        printButton.addClickListener(e -> confirmFileTypeTransaction(Collections.singletonList(transaction)));
        return printButton;
    }


    private Button printAllTransactions() {
        Button printButton = new Button("Print all transactions", new Icon(VAADIN, PRINT));
        printButton.addClickListener(e -> {
            List<Transaction> allTransactions = transactionRepository.findAll().list();
            confirmFileTypeTransaction(allTransactions);

        });
        return printButton;
    }


    private void selectedTransactions(){

        List<Transaction> allTransactions = transactionRepository.findAll().list();
        List<Transaction> selectedTransactions = new ArrayList<>();

        if(!selectedRow.isEmpty()){
            selectedRow.forEach((id, isChecked) -> {
                if(isChecked){
                    selectedTransactions.add(allTransactions.stream().filter(t ->
                            t.getId().equals(id)).findFirst().get());
                    confirmFileTypeTransaction(selectedTransactions);
                }else{
                    Utils.showErrorMessage("Please, select at least one transaction");
                }
            });

        }else{
            Utils.showErrorMessage("Please, select at least one transaction");
        }
    }


    private Button printSelectedTransactions() {
        Button printButton = new Button("Print selected transactions", new Icon(VAADIN, PRINT));
        printButton.setThemeName("success");

        printButton.addClickListener(e -> selectedTransactions());
        return printButton;
    }


    private void confirmFileTypeTransaction(List<Transaction> selectedTransactions){

        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Select type of file");
        dialog.setText("Please, select a type of file. Do you want CSV or PDF file?");

        dialog.setCancelable(true);

        dialog.setRejectable(true);
        dialog.setRejectText("PDF");
        dialog.setRejectButtonTheme("error primary");
        dialog.setConfirmText("CSV");
        dialog.setConfirmButtonTheme("success primary");

        dialog.addRejectListener(event -> generatePdf(selectedTransactions));
        dialog.addConfirmListener(event -> generateCsv(selectedTransactions));

        Button button = new Button("Open confirm dialog");
        button.addClickListener(event -> dialog.open());
        dialog.open();

    }


    public void fillGridWithData(){
        List<Transaction> transactions = transactionRepository.findAll().list();
        transactions.sort(Comparator.comparing(Transaction::getId));
        transactionGrid.setItems(transactions);
    }


    //Verificar este metodo
    private void filterTransactions(String filterText) {
        List<Transaction> filteredTransactions;

        if (filterText == null || filterText.isEmpty()) {
            filteredTransactions = transactionRepository.findAll().list();
        } else {
            List<Transaction> transactionList =  transactionRepository.findAll().list();
            filteredTransactions = transactionList.stream().filter(transaction ->
                    transaction.getClient().getName().toLowerCase().contains(filterText.toLowerCase()) ||
                            transaction.getClient().getLastName().toLowerCase().contains(filterText.toLowerCase()) ||
                            transaction.getProduct().getName().toLowerCase().contains(filterText.toLowerCase()) ||
                            transaction.getProduct().getCode().toLowerCase().contains(filterText.toLowerCase()) ||
                            transaction.getProduct().getPrice().toString().contains(filterText) ||
                            transaction.getQuantityTransaction().toString().toLowerCase().contains(filterText.toLowerCase()) ||
                            transaction.getTransactionDate().toString().contains(filterText) ||
                            transaction.getProduct().getRegistryDate().toString().contains(filterText)

            ).toList();

        }
        transactionGrid.setItems(filteredTransactions);

    }


    public void generateCsv(List<Transaction> transaction) {
        String filePath = "Transaction.csv";
        csvService.generateArrayCsv(filePath, transaction);
        File file = new File(filePath);
        Utils.showInfoMessage("File generated successfully");
        downloadFile(file, "csv");
    }

    public void generatePdf(List<Transaction> transaction) {
        String filePath = "Transaction.pdf";
        pdfService.generatePdf(filePath, transaction);
        File file = new File(filePath);
        Utils.showInfoMessage("File generated successfully");
        downloadFile(file, "pdf");
    }

    private void downloadFile(File file, String extension) {
        byte[] bytes;
        try {
            bytes = file.exists() ? Files.readAllBytes(file.toPath()) : new byte[0];
            StreamResource resource = new StreamResource("Transaction." + extension,
                    () -> new ByteArrayInputStream(bytes));
            Anchor anchor = new Anchor(resource, "");
            anchor.getElement().setAttribute("download", true);
            anchor.getElement().callJsFunction("click");
            getContent().add(anchor);
        } catch (IOException e) {
            Utils.showErrorMessage("An error occurred while trying to download the file");
        }
    }

}
