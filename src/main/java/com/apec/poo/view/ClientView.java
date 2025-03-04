package com.apec.poo.view;

import com.apec.poo.entities.Client;
import com.apec.poo.repository.ClientRepository;
import com.apec.poo.repository.TransactionRepository;
import com.apec.poo.utils.CustomException;
import com.apec.poo.utils.Utils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.HashMap;
import java.util.Map;


@PageTitle("Client")
@Route("client")
@Menu(order = 0, title = "Clients", icon = LineAwesomeIconUrl.USER)
public class ClientView extends ClientViewAbstract {

    private static final String FULL_WIDTH = "100%";
    private static final String MAX_WIDTH = "800px";
    private static final String MIN_CONTENT = "min-content";
    private final ClientRepository clientRepository;
    private final TextField firstNameField = createNameField();
    private final TextField lastNameField = createLastNameField();
    private final TextField phoneField = createPhoneNumberField();
    private final EmailField emailField = createEmailField();
    private final Tab registerClientTab = new Tab("Register client");
    private final Tab allClientsTab = new Tab("All clients");
    private final TabSheet mainTabSheet = new TabSheet();
    private final ComboBox<String> countryCodeCombo = createCountryCodeField();



    @Inject
    public ClientView(ClientRepository clientRepository, TransactionRepository transactionRepository) {
        this.clientRepository = clientRepository;
        VerticalLayout mainLayout = createMainLayout();
        VerticalLayout content = new VerticalLayout();
        FormLayout formLayout = createFormLayout();
        ClientGridView clientGridView = new ClientGridView(clientRepository, transactionRepository);
        HorizontalLayout buttonLayout = saveButtonLayout();
        mainTabSheet.setWidth(FULL_WIDTH);
        mainTabSheet.add(registerClientTab, mainLayout);
        mainTabSheet.add(allClientsTab, clientGridView);
        mainTabSheet.addSelectedChangeListener(event -> {
            Tab tab = event.getSelectedTab();
            if (tab.getLabel().equals("All clients")) {
                clientGridView.fillGridWithData();

            }
        });


        content.add(mainTabSheet);
        mainLayout.add(new H3("Client Information"), formLayout, buttonLayout);
        getContent().add(content);
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

    private FormLayout createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.setWidth(FULL_WIDTH);
        HorizontalLayout phoneLayout = new HorizontalLayout();
        phoneLayout.add(countryCodeCombo, phoneField);
        formLayout.add(firstNameField, lastNameField, phoneLayout, emailField);
        return formLayout;
    }

    private HorizontalLayout saveButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName(Gap.MEDIUM);
        buttonLayout.setWidth(FULL_WIDTH);
        buttonLayout.getStyle().set("flex-grow", "1");

        Button saveButton = new Button("Save", new Icon("vaadin", "check"));
        saveButton.setWidth(MIN_CONTENT);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> saveClient());

        Button cancelButton = new Button("Cancel", new Icon("vaadin", "close"));
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(e -> {
            clearFields();
            Utils.showContrastMessage("Operation aborted");
        });

        cancelButton.setWidth(MIN_CONTENT);
        buttonLayout.add(saveButton, cancelButton);
        return buttonLayout;
    }

    private TextField createNameField() {
        TextField nameField = new TextField("First Name");
        nameField.setWidth(FULL_WIDTH);
        nameField.setRequired(true);
        nameField.setPlaceholder("John");
        nameField.addValueChangeListener(event ->
                nameFieldValidationListener(event, nameField));
        return nameField;
    }


    private TextField createLastNameField() {
        TextField lastName = new TextField("Last Name");
        lastName.setWidth(FULL_WIDTH);
        lastName.setRequired(true);
        lastName.setPlaceholder("Doe");
        lastName.addValueChangeListener(event -> lastNameFieldValidationListener(event, lastName));
        return lastName;
    }


    private TextField createPhoneNumberField() {
        TextField field = new TextField("Phone Number");
        field.setWidthFull();
        field.setRequired(true);
        field.setPlaceholder("123-456-7890");
        field.addValueChangeListener(event -> phoneFieldValidationListener(event, field));
        return field;
    }


    @Transactional
    public void saveClient() {
        if (isInvalidFields()) {
            return;
        }
        try {
            Client client = createClient();
            clientRepository.persist(client);
            Utils.showInfoMessage("Client saved");
            clearFields();
        } catch (CustomException e) {
            Utils.showErrorMessage(e.getMessage());
        } catch (Exception e) {
            Utils.showErrorMessage("An error occurred while trying to save the client");
        }

    }

    private Client createClient() {
        Client client = new Client();
        client.setName(firstNameField.getValue());
        client.setLastName(lastNameField.getValue());
        client.setPhoneNumber(phoneField.getValue());
        client.setCountryCode(countryCodeCombo.getValue());
        client.setEmail(emailField.getValue());
        return client;
    }

    private boolean isInvalidFields() {
        Map<String, Boolean> invalidInput = new HashMap<>();
        invalidInput.put("First Name", firstNameField.isInvalid());
        invalidInput.put("Last Name", lastNameField.isInvalid());
        invalidInput.put("Phone", phoneField.isInvalid());
        invalidInput.put("Country Code", countryCodeCombo.isInvalid());
        invalidInput.put("Email", emailField.isInvalid());

        for (Map.Entry<String, Boolean> entry : invalidInput.entrySet()) {
            if (entry.getValue()) {
                Utils.showErrorMessage("Invalid " + entry.getKey());
                return true;
            }
        }

        Map<String, Boolean> emptyInput = new HashMap<>();
        emptyInput.put("First Name", firstNameField.getValue().isBlank());
        emptyInput.put("Last Name", lastNameField.getValue().isBlank());
        emptyInput.put("Phone", phoneField.getValue().isBlank());
        emptyInput.put("Country Code", (countryCodeCombo.getValue() == null || countryCodeCombo.getValue().isBlank()));
        emptyInput.put("Email", emailField.getValue().isBlank());

        for (Map.Entry<String, Boolean> entry : emptyInput.entrySet()) {
            if (entry.getValue()) {
                Utils.showErrorMessage("Required Field: " + entry.getKey());
                return true;
            }
        }

        return false;
    }

    private void clearFields() {
        firstNameField.clear();
        firstNameField.setInvalid(false);
        lastNameField.clear();
        lastNameField.setInvalid(false);
        phoneField.clear();
        phoneField.setInvalid(false);
        emailField.clear();
        emailField.setInvalid(false);
        firstNameField.focus();
        countryCodeCombo.clear();
        countryCodeCombo.setInvalid(false);
    }


}



