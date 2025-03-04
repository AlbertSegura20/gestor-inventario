package com.apec.poo.view;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;

public class ClientViewAbstract extends Composite<VerticalLayout> {

    public void nameFieldValidationListener(AbstractField.ComponentValueChangeEvent<TextField, String> event,
                                            TextField nameField){
        String value = event.getValue();
        if (value != null && value.matches("^[\\p{Punct}\\s]*$")) {
            nameField.setInvalid(true);
            nameField.setErrorMessage("Invalid name.");
        } else {
            nameField.setInvalid(false);
        }
    }

    public void lastNameFieldValidationListener(AbstractField.ComponentValueChangeEvent<TextField, String> event,
                                                TextField lastName){
        String value = event.getValue();
        if (value != null && value.matches("^[\\p{Punct}\\s]*$")) {
            lastName.setInvalid(true);
            lastName.setErrorMessage("Invalid last name.");
        } else {
            lastName.setInvalid(false);
        }
    }

    public EmailField createEmailField() {
        EmailField emailField = new EmailField("Email");
        emailField.setPlaceholder("Example@gmail.com");
        emailField.setWidth("100%");
        emailField.setRequired(true);
        emailField.setRequiredIndicatorVisible(true);
        emailField.setPattern("^[a-zA-Z0-9_\\-+]+(?:\\.[a-zA-Z0-9_\\-+]+)*@[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,}$");
        emailField.setI18n(new EmailField.EmailFieldI18n()
                .setRequiredErrorMessage("Field is required")
                .setPatternErrorMessage("Enter a valid email address"));

        return emailField;
    }

    public void phoneFieldValidationListener(AbstractField.ComponentValueChangeEvent<TextField, String> event,
                                             TextField field){
        String value = event.getValue().replaceAll("[^0-9]", "");
        if (value.length() == 10) {
            value = value.substring(0, 3) + "-" + value.substring(3, 6) + "-" + value.substring(6);
        }

        field.setValue(value);
        String phoneRegex = "^\\d{3}-\\d{3}-\\d{4}$";
        if (!value.matches(phoneRegex)) {
            field.setInvalid(true);
            field.setErrorMessage("Accepted format: XXX-XXX-XXXX");
        } else {
            field.setInvalid(false);
        }
    }

    public ComboBox<String> createCountryCodeField() {
        ComboBox<String> code = new ComboBox<>("Country Code");
        code.setItems("+1", "+91", "+44", "+61");
        code.setPlaceholder("Select Code");
        code.setRequired(true);
        code.setWidth(40, Unit.PERCENTAGE);
        code.setRequiredIndicatorVisible(true);
        code.addValueChangeListener(e -> {
            if (e.getValue() == null || e.getValue().isEmpty()) {
                code.setInvalid(true);
                code.setErrorMessage("Please select a country code.");
            } else {
                code.setInvalid(false);
            }
        });
        return code;
    }
}
