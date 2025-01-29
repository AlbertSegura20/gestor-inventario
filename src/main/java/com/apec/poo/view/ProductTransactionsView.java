package com.apec.poo.view;


import com.apec.poo.entities.Client;
import com.apec.poo.entities.Product;
import com.apec.poo.repository.ClientRepository;
import com.apec.poo.repository.ProductRepository;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
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

import java.util.ArrayList;
import java.util.List;


@PageTitle("Product Transactions")
@Route("transactions")
@Menu(order = 2)
public class ProductTransactionsView extends Composite<VerticalLayout> {

    private static final String FULL_WIDTH = "100%";
    private static final String MIN_CONTENT = "min-content";
    private static final String MAX_WIDTH = "800px";
    private final ComboBox<SampleItem> clientNameComboBox = new ComboBox<>("Client name");
    private final TextField quantityField = new TextField("Quantity");
    private final EmailField productField = new EmailField("Product");
    private final DatePicker registrationDatePicker = new DatePicker("Registration Date");
    private final TextField priceField = new TextField("Price");
    @Inject
    ClientRepository clientRepository;
    @Inject
    ProductRepository productRepository;

    public ProductTransactionsView() {
        initializeContent();
        VerticalLayout mainLayout = createMainLayout();
        mainLayout.add(createHeader(), createFormLayout(), createLayoutRow());
        getContent().add(mainLayout);
    }

    private void initializeContent() {
        getContent().setWidth(FULL_WIDTH);
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
    }

    private VerticalLayout createMainLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth(FULL_WIDTH);
        layout.setMaxWidth(MAX_WIDTH);
        layout.setHeight("min-content");
        return layout;
    }

    private H3 createHeader() {
        H3 header = new H3("Product Transactions");
        header.setWidth(FULL_WIDTH);
        return header;
    }

    private FormLayout createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.setWidth(FULL_WIDTH);
        clientNameComboBox.setWidth(MIN_CONTENT);
        setComboBoxSampleData(clientNameComboBox);
        registrationDatePicker.setWidth(MIN_CONTENT);

        formLayout.add(clientNameComboBox, quantityField, productField, registrationDatePicker, priceField);
        return formLayout;
    }

    private HorizontalLayout createLayoutRow() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName(Gap.MEDIUM);
        buttonLayout.setWidth(FULL_WIDTH);
        buttonLayout.getStyle().set("flex-grow", "1");

        Button saveButton = new Button("Save");
        saveButton.setWidth(MIN_CONTENT);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel");
        cancelButton.setWidth(MIN_CONTENT);



        buttonLayout.add(saveButton, cancelButton);
        return buttonLayout;
    }

    record SampleItem(String value, String label, Boolean disabled) {
    }

    private void setComboBoxSampleData(ComboBox comboBox) {
        List<SampleItem> sampleItems = new ArrayList<>();
        sampleItems.add(new SampleItem("first", "First", null));
        sampleItems.add(new SampleItem("second", "Second", null));
        sampleItems.add(new SampleItem("third", "Third", Boolean.TRUE));
        sampleItems.add(new SampleItem("fourth", "Fourth", null));
        comboBox.setItems(sampleItems);
        comboBox.setItemLabelGenerator(item -> ((SampleItem) item).label());
    }

    private void getProductAndClientInformation(){


    }
}