package com.apec.poo.view;


import com.apec.poo.entities.Product;
import com.apec.poo.entities.ProductStatus;
import com.apec.poo.repository.ProductRepository;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@PageTitle("Product")
@Route("product")
@Menu(order = 1)
public class ProductView extends Composite<VerticalLayout> {

    @Inject
    ProductRepository productRepository;
    private static final String FULL_WIDTH = "100%";
    private static final String MAX_WIDTH = "800px";
    private static final String MIN_CONTENT = "min-content";
    private final TextField nameField = createTextField("Name");
    private final TextField quantityField = createQuantityField();
    private final EmailField descriptionField = new EmailField("Description");
    private final ComboBox<SampleItem> statusComboBox = createComboBox("Status");
    private final DatePicker registrationDatePicker = createDatePicker("Registration Date");
    private final TextField priceField = createPriceField();


    public ProductView() {
        configureContent(getContent());
        VerticalLayout mainLayout = createMainLayout();
        mainLayout.add(createHeader(), createForm(), saveButtonLayout());
        getContent().add(mainLayout);
    }

    private void configureContent(VerticalLayout content) {
        content.setWidth(FULL_WIDTH);
        content.getStyle().set("flex-grow", "1");
        content.setJustifyContentMode(JustifyContentMode.START);
        content.setAlignItems(Alignment.CENTER);
    }

    private VerticalLayout createMainLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidth(FULL_WIDTH);
        mainLayout.setMaxWidth(MAX_WIDTH);
        mainLayout.setHeight("min-content");
        return mainLayout;
    }

    private H3 createHeader() {
        H3 header = new H3("Product Information");
        header.setWidth(FULL_WIDTH);
        return header;
    }

    private FormLayout createForm() {
        FormLayout formLayout = new FormLayout();
        formLayout.setWidth(FULL_WIDTH);
        formLayout.add(nameField, quantityField, descriptionField, statusComboBox, registrationDatePicker, priceField);
        return formLayout;
    }


    private HorizontalLayout saveButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName(Gap.MEDIUM);
        buttonLayout.setWidth(FULL_WIDTH);
        buttonLayout.getStyle().set("flex-grow", "1");

        Button saveButton = createPrimaryButton("Save");
        Button cancelButton = createButton("Cancel");
        saveButton.addClickListener(e -> saveProduct());

        buttonLayout.add(saveButton, cancelButton);
        return buttonLayout;
    }

    private TextField createTextField(String label) {
        TextField textField = new TextField(label);
        return textField;
    }

    private ComboBox<SampleItem> createComboBox(String label) {
        ComboBox<SampleItem> comboBox = new ComboBox<>(label);
        comboBox.setWidth(MIN_CONTENT);
        setComboBoxSampleData(comboBox);
        return comboBox;
    }

    private DatePicker createDatePicker(String label) {
        DatePicker datePicker = new DatePicker(label);
        datePicker.setWidth(MIN_CONTENT);
        return datePicker;
    }


    private TextField createQuantityField() {
        TextField quantityField = new TextField("Quantity");
        quantityField.setWidth(MIN_CONTENT);
        quantityField.setValue("1");
        return quantityField;
    }


    private TextField createPriceField() {
        TextField priceField = new TextField("Price");
        priceField.setWidth(MIN_CONTENT);
        priceField.setValue("0");
        Div dollarPrefix = new Div();
        dollarPrefix.setText("$");
        priceField.setPrefixComponent(dollarPrefix);
        return priceField;
    }

    private Button createPrimaryButton(String text) {
        Button button = new Button(text);
        button.setWidth(MIN_CONTENT);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return button;
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setWidth(MIN_CONTENT);
        return button;
    }

    record SampleItem(ProductStatus status, String label, boolean selected) {
    }

    private void setComboBoxSampleData(ComboBox<SampleItem> comboBox) {

        List<SampleItem> productStatus = new ArrayList<>();
        productStatus.add(new SampleItem(ProductStatus.AVAILABLE, "Available Product", false));
        productStatus.add(new SampleItem(ProductStatus.UNAVAILABLE, "Unavailable Product", false));
        comboBox.setItems(productStatus);
        comboBox.setItemLabelGenerator(SampleItem::label);

    }


    public Product createProduct(){
        Product product = new Product();
        product.setName(nameField.getValue());
        product.setDescription(descriptionField.getValue());
        product.setStatus(statusComboBox.getValue().status);
        product.setQuantity(Integer.parseInt(quantityField.getValue()));
        product.setPrice(BigDecimal.valueOf(Double.parseDouble(priceField.getValue())));
        product.setRegistryDate(registrationDatePicker.getValue());

        return product;

    }


    @Transactional
    public void saveProduct(){
        Product product = createProduct();
        productRepository.persist(product);

    }

}