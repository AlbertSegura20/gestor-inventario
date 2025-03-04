package com.apec.poo.view;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

public class ProductViewAbstract extends Composite<VerticalLayout> {

    private static final String MIN_CONTENT = "min-content";

    public TextField createNameField() {
        TextField name = new TextField("Name");
        name.setPlaceholder("Enter name");
        name.addValueChangeListener(e -> textFieldValidationListener(e, name, "Invalid name."));
        name.setRequired(true);
        name.setRequiredIndicatorVisible(true);
        return name;
    }

    public TextField createDescriptionField() {
        TextField description = new TextField("Description");
        description.setPlaceholder("Enter description");
        description.addValueChangeListener(e -> textFieldValidationListener(e, description, "Invalid description."));
        description.setRequired(true);
        description.setRequiredIndicatorVisible(true);
        return description;
    }

    public TextField createCodeField() {
        TextField codeField = new TextField("Product code");
        codeField.setWidth(MIN_CONTENT);
        codeField.setPlaceholder("Enter product code");
        codeField.addValueChangeListener(e -> textFieldValidationListener(e, codeField, "Invalid code."));
        codeField.setRequired(true);
        codeField.setRequiredIndicatorVisible(true);
        return codeField;
    }

    public TextField createQuantityField() {
        TextField quantity = new TextField("Quantity");
        quantity.setWidth(MIN_CONTENT);
        quantity.setValue("1");
        quantity.addValueChangeListener(e -> textFieldValidationListener(e, quantity, "Invalid quantity."));
        quantity.setRequired(true);
        quantity.setRequiredIndicatorVisible(true);
        return quantity;
    }

    public NumberField createPriceField() {
        NumberField price = new NumberField("Price");
        price.setWidth(MIN_CONTENT);
        price.setPlaceholder("0");
        Div dollarPrefix = new Div();
        dollarPrefix.setText("$");
        price.setPrefixComponent(dollarPrefix);
//        price.addValueChangeListener(e -> textFieldValidationListener(e, price, "Invalid price."));
        price.setRequired(true);
        price.setRequiredIndicatorVisible(true);
        return price;
    }

    public void textFieldValidationListener(AbstractField.ComponentValueChangeEvent<TextField, String> event,
                                            TextField nameField, String errorMessage){
        String value = event.getValue();
        if (value != null && value.matches("^[\\p{Punct}\\s]*$")) {
            nameField.setInvalid(true);
            nameField.setErrorMessage(errorMessage);
        } else {
            nameField.setInvalid(false);
        }
    }

    public void textFieldValidationListener(AbstractField.ComponentValueChangeEvent<NumberField, String> event,
                                            NumberField nameField, String errorMessage){
        String value = event.getValue();
        if (value != null && value.matches("^[\\p{Punct}\\s]*$")) {
            nameField.setInvalid(true);
            nameField.setErrorMessage(errorMessage);
        } else {
            nameField.setInvalid(false);
        }
    }
}

