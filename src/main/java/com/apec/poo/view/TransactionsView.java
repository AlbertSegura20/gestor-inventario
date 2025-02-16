package com.apec.poo.view;

import com.apec.poo.entities.Client;
import com.apec.poo.entities.Product;
import com.apec.poo.entities.ProductStatus;
import com.apec.poo.entities.Transaction;
import com.apec.poo.repository.ClientRepository;
import com.apec.poo.repository.ProductRepository;
import com.apec.poo.repository.TransactionRepository;
import com.apec.poo.utils.CustomException;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;


@PageTitle("Transactions")
@Route("transactions")
@Menu(order = 2)
public class TransactionsView extends Composite<VerticalLayout> {


    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private static final String FULL_WIDTH = "100%";
    private static final String MIN_CONTENT = "min-content";
    private static final String MAX_WIDTH = "800px";
    private final ComboBox<Client> clientNameComboBox = new ComboBox<>("Client name");
    private final NumberField quantityField = createQuantityField();
    private final TextField productCodeField = createCodeField();
    private final NumberField totalPriceField = createTotalPriceField();
    private final NumberField totalQuantityField = createTotalQuantityField();
    private final TextField registrationDateField = createRegistrationDateField();
    private final ComboBox<Product> productNameComboBox = new ComboBox<>("Product name");
    private final DatePicker transactionDatePicker = new DatePicker("Transaction date");
    private boolean eventHandled = false;
    private final NumberField priceField = createPriceField();
    Tab tab1 = new Tab("Register transaction");
    Tab tab2 = new Tab("All transactions");
    TabSheet tabs = new TabSheet();


    @Inject
    public TransactionsView(ClientRepository clientRepository, ProductRepository productRepository, TransactionRepository transactionRepository) {
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
        initializeContent();
        VerticalLayout mainLayout = createMainLayout();
        VerticalLayout content = new VerticalLayout();

        tabs.setWidth(FULL_WIDTH);
        tabs.add(tab1, mainLayout);
        tabs.add(tab2, new TransactionsGridView(transactionRepository));

        content.add(tabs);
        mainLayout.add(createHeader(), createFormLayout(), createLayoutRow());
        getContent().add(content);

    }

    private void initializeContent() {
        getContent().setWidth(FULL_WIDTH);
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        getClientInformation();
    }

    private VerticalLayout createMainLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth(FULL_WIDTH);
        layout.setMaxWidth(MAX_WIDTH);
        layout.setHeight(MIN_CONTENT);
        return layout;
    }

    private H3 createHeader() {
        H3 header = new H3("Transaction");
        header.setWidth(FULL_WIDTH);
        return header;
    }

    private FormLayout createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.setWidth(FULL_WIDTH);
        clientNameComboBox.setWidth(MIN_CONTENT);
        productNameComboBox.setWidth(MIN_CONTENT);
        setComboBoxClientData(clientNameComboBox);
        setComboBoxProductData(productNameComboBox);
        transactionDatePicker.setWidth(MIN_CONTENT);

        formLayout.add(clientNameComboBox, productNameComboBox, productCodeField,
                             quantityField, registrationDateField, priceField, totalQuantityField,
                             totalPriceField, transactionDatePicker);
        return formLayout;
    }

    private NumberField createQuantityField() {
        NumberField quantity = new NumberField("Quantity");
        quantity.setWidth(MIN_CONTENT);
        quantity.setReadOnly(true);
        return quantity;
    }


    private NumberField createPriceField() {
        NumberField price = new NumberField("Price");
        price.setWidth(MIN_CONTENT);
        price.setValue(0.0);
        price.setReadOnly(true);
        Div dollarPrefix = new Div();
        dollarPrefix.setText("$");
        price.setPrefixComponent(dollarPrefix);
        return price;
    }

    private TextField createCodeField() {
        TextField codeField = new TextField("Product code");
        codeField.setWidth(MIN_CONTENT);
        codeField.setReadOnly(true);
        return codeField;
    }


    private NumberField createTotalPriceField() {
        NumberField totalPrice = new NumberField("Total price");
        totalPrice.setWidth(MIN_CONTENT);
        totalPrice.setValue(0.0);
        totalPrice.setReadOnly(true);
        Div dollarPrefix = new Div();
        dollarPrefix.setText("$");
        totalPrice.setPrefixComponent(dollarPrefix);

        return totalPrice;
    }



    private NumberField createTotalQuantityField() {
        NumberField totalQuantity = new NumberField("Total quantity");
        totalQuantity.setWidth(MIN_CONTENT);
        totalQuantity.setPlaceholder("0.00");
        Div dollarPrefix = new Div();
        dollarPrefix.setText("$");
        totalQuantity.setPrefixComponent(dollarPrefix);

        totalQuantity.addValueChangeListener(e -> {
            if (e.getValue() == null) {
                totalQuantity.setValue(0.0);
            }
            if (priceField.getValue() == null) {
                priceField.setValue(0.0);
            }

            if(!eventHandled){

                if(e.getValue() < 0.0){
                    Notification notification = Notification.show("La cantidad debe ser mayor a 0", 3000, Notification.Position.BOTTOM_CENTER);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
                if(e.getValue() > quantityField.getValue()){
                    Notification notification = Notification.show("La cantidad debe ser menor o igual a la cantidad disponible", 3000, Notification.Position.BOTTOM_CENTER);
                    notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
                }

            }

            Double totalPrice = totalQuantity.getValue() * priceField.getValue();
            totalPriceField.setValue(totalPrice);
        });
        return totalQuantity;
    }


    private TextField createRegistrationDateField() {
        TextField registrationDate = new TextField("Registration date");
        registrationDate.setWidth(MIN_CONTENT);
        registrationDate.setReadOnly(true);
        return registrationDate;
    }

    private HorizontalLayout createLayoutRow() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName(Gap.MEDIUM);
        buttonLayout.setWidth(FULL_WIDTH);
        buttonLayout.getStyle().set("flex-grow", "1");

        Button saveButton = new Button("Save");
        saveButton.setWidth(MIN_CONTENT);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        saveButton.addClickListener(e -> saveTransaction());

        Button cancelButton = new Button("Cancel");
        /// Revisar, no borra del todo
        cancelButton.addClickListener(e -> {
            clearFields();
            Notification notification = Notification.show("Operacion cancelada", 3000, Notification.Position.BOTTOM_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        });

        cancelButton.setWidth(MIN_CONTENT);

        buttonLayout.add(saveButton, cancelButton);
        return buttonLayout;
    }

    private void setComboBoxClientData(ComboBox<Client> comboBox) {
         List<Client> clientItems = getClientInformation();
         comboBox.setItems(clientItems);
         comboBox.setItemLabelGenerator(item -> item.getName() + " " + item.getLastName());
    }


    private List<Client> getClientInformation() {
        return clientRepository.findAll().list();
    }

    private void setComboBoxProductData(ComboBox<Product> comboBox) {
        List<Product> productItems = getProductInformation();
        comboBox.setItems(productItems.stream().filter(item -> item.getStatus() == ProductStatus.AVAILABLE).toList());
        comboBox.setItemLabelGenerator(Product::getName);

        /// Mover codigo a otro metodo
        comboBox.addValueChangeListener(e -> {

            if (e.getValue() != null) {
                Long selectedItem = e.getValue().getId();

                productRepository.find("id", selectedItem).firstResultOptional().ifPresent(product -> {
                    priceField.setValue(product.getPrice().doubleValue());
                    quantityField.setValue((double) product.getQuantity());
                    registrationDateField.setValue(String.valueOf(product.getRegistryDate()));
                    productCodeField.setValue(String.valueOf(product.getCode()));
                });

            }
        });

    }

    private List<Product> getProductInformation() {
        return productRepository.findAll().list();
    }

    private Transaction createTransaction(){

        Transaction transaction = new Transaction();
        transaction.setClient(clientNameComboBox.getValue());
        transaction.setProduct(productNameComboBox.getValue());
        transaction.setQuantityTransaction(totalQuantityField.getValue());
        transaction.setTransactionDate(transactionDatePicker.getValue().atStartOfDay());

        double totalPrice =  priceField.getValue() * quantityField.getValue();
        transaction.setTotalPrice(BigDecimal.valueOf(totalPrice));

        return transaction;
    }

    @Transactional
    public void saveTransaction(){
        try {
            Transaction transaction = createTransaction();
            transactionRepository.persist(transaction);
            Product product = transaction.getProduct();
            product = productRepository.findById(product.getId());

            double quantityProductLeft = transaction.getProduct().getQuantity() -  transaction.getQuantityTransaction();
            product.setQuantity((int) quantityProductLeft);
            productRepository.persist(product);

            Notification notification = Notification.show("Transaccion guardada", 3000, Notification.Position.BOTTOM_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            eventHandled = true;

            // Clearing text fields
            clearFields();

        }catch (CustomException e){
            Notification notification = Notification.show(e.getMessage(), 3000, Notification.Position.BOTTOM_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }catch (Exception e) {
            Notification notification = Notification.show("Se produjo un error intentando realizar la transaccion", 3000, Notification.Position.BOTTOM_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }

        eventHandled = false;

    }


    private void clearFields(){
        transactionDatePicker.clear();
        registrationDateField.clear();
        totalPriceField.clear();
        productCodeField.clear();
        quantityField.clear();
        priceField.clear();
        totalQuantityField.clear();

        // Clearing date picker
        clientNameComboBox.clear();
        productNameComboBox.clear();

    }
}