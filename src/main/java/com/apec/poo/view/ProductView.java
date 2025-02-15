package com.apec.poo.view;

import com.apec.poo.entities.Product;
import com.apec.poo.entities.ProductStatus;
import com.apec.poo.repository.ProductRepository;
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


    private final ProductRepository productRepository;
    private static final String FULL_WIDTH = "100%";
    private static final String MAX_WIDTH = "800px";
    private static final String MIN_CONTENT = "min-content";
    private final TextField nameField = createTextField();
    private final TextField quantityField = createQuantityField();
    private final TextField productCodeField = createCodeField();
    private final EmailField descriptionField = new EmailField("Description");
    private final ComboBox<SampleItem> statusComboBox = createComboBox();
    private final DatePicker registrationDatePicker = createDatePicker();
    private final TextField priceField = createPriceField();


    @Inject
    public ProductView(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
        mainLayout.setHeight(MIN_CONTENT);
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
        formLayout.add(productCodeField, nameField, quantityField, descriptionField, statusComboBox, registrationDatePicker, priceField);
        return formLayout;
    }


    private HorizontalLayout saveButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName(Gap.MEDIUM);
        buttonLayout.setWidth(FULL_WIDTH);
        buttonLayout.getStyle().set("flex-grow", "1");

        Button saveButton = createPrimaryButton();
        Button cancelButton = createButton();
        saveButton.addClickListener(e -> saveProduct());

        buttonLayout.add(saveButton, cancelButton);
        return buttonLayout;
    }

    private TextField createTextField() {
        return new TextField("Name");
    }

    private ComboBox<SampleItem> createComboBox() {
        ComboBox<SampleItem> comboBox = new ComboBox<>("Status");
        comboBox.setWidth(MIN_CONTENT);
        setComboBoxSampleData(comboBox);
        return comboBox;
    }

    private DatePicker createDatePicker() {
        DatePicker datePicker = new DatePicker("Registration Date");
        datePicker.setWidth(MIN_CONTENT);
        return datePicker;
    }


    private TextField createQuantityField() {
        TextField quantity = new TextField("Quantity");
        quantity.setWidth(MIN_CONTENT);
        quantity.setValue("1");
        return quantity;
    }


    private TextField createPriceField() {
        TextField price = new TextField("Price");
        price.setWidth(MIN_CONTENT);
        price.setPlaceholder("0");
        Div dollarPrefix = new Div();
        dollarPrefix.setText("$");
        price.setPrefixComponent(dollarPrefix);
        return price;
    }

    private TextField createCodeField() {
        TextField codeField = new TextField("Product code");
        codeField.setWidth(MIN_CONTENT);
        codeField.setPlaceholder("xxxx-xxxx-xxxx");
        return codeField;
    }

    private Button createPrimaryButton() {
        Button button = new Button("Save");
        button.setWidth(MIN_CONTENT);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return button;
    }

    private Button createButton() {
        Button button = new Button("Cancel");
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
        product.setCode(productCodeField.getValue());
        validateFields(product);

        return product;

    }


    @Transactional
    public void saveProduct(){
        try {
            Product product = createProduct();
            productRepository.persist(product);
            Notification notification = Notification.show("Producto guardado", 3000, Notification.Position.BOTTOM_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            // Clearing text fields
            clearFields();

        }catch (CustomException e){
            Notification notification = Notification.show(e.getMessage(), 3000, Notification.Position.BOTTOM_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }catch (Exception e) {
            Notification notification = Notification.show("Se produjo un error intentando realizar la transaccion", 3000, Notification.Position.BOTTOM_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }

    }


    private void validateFields(Product product) {
        if (product.getName().isBlank() || product.getDescription().isBlank() || product.getStatus() == null || product.getQuantity() <= 0 || product.getPrice().doubleValue() <= 0 || product.getRegistryDate() == null || product.getCode().isBlank() ) {
            throw new CustomException("Todos los campos son requeridos");}
    }



    private void clearFields() {
        nameField.clear();
        descriptionField.clear();
        statusComboBox.clear();
        registrationDatePicker.clear();
        priceField.clear();
        quantityField.clear();
        productCodeField.clear();
    }

}