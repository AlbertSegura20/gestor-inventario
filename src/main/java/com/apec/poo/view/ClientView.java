package com.apec.poo.view;

import com.apec.poo.entities.Client;
import com.apec.poo.repository.ClientRepository;
import com.apec.poo.repository.TransactionRepository;
import com.apec.poo.utils.CustomException;
import com.apec.poo.utils.Utils;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
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



@PageTitle("Person Form")
@Route("client")
@Menu(order = 0, title = "Client")
public class ClientView extends Composite<VerticalLayout> {


    private final ClientRepository clientRepository;
    TextField firstNameField = createNameField();
    TextField lastNameField = createLastNameField();
    TextField phoneField = new TextField("Phone Number");
    EmailField emailField = createEmailField();
    private static final String FULL_WIDTH = "100%";
    private static final String MAX_WIDTH = "800px";
    private static final String MIN_CONTENT = "min-content";
    Tab tab1 = new Tab("Register client");
    Tab tab2 = new Tab("All clients");
    TabSheet tabs = new TabSheet();

    @Inject
    public ClientView(ClientRepository clientRepository , TransactionRepository transactionRepository) {
        this.clientRepository = clientRepository;
        VerticalLayout mainLayout = createMainLayout();
        VerticalLayout content = new VerticalLayout();
        FormLayout formLayout = createFormLayout();
        ClientGridView clientGridView = new ClientGridView(clientRepository, transactionRepository);
        HorizontalLayout buttonLayout = saveButtonLayout();
        tabs.setWidth(FULL_WIDTH);
        tabs.add(tab1, mainLayout);
        tabs.add(tab2, clientGridView);
        tabs.addSelectedChangeListener(event -> {
            Tab tab = event.getSelectedTab();
            if (tab.getLabel().equals("All clients")){
                clientGridView.fillGridWithData();

            }
        });


        content.add(tabs);
        mainLayout.add(new H3("Client Information"), formLayout, buttonLayout);
        getContent().add(content);
//        setPhoneNumberMask();
        PhoneNumberMaskView();

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
        formLayout.add(firstNameField, lastNameField, phoneField, emailField);
        return formLayout;
    }

    private HorizontalLayout saveButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName(Gap.MEDIUM);
        buttonLayout.setWidth(FULL_WIDTH);
        buttonLayout.getStyle().set("flex-grow", "1");

        Button saveButton = new Button("Save");
        saveButton.setWidth(MIN_CONTENT);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> saveClient());

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e -> {
            clearFields();
            Utils.showContrastMessage("Operation aborted");
        });

        cancelButton.setWidth(MIN_CONTENT);

        buttonLayout.add(saveButton, cancelButton);
        return buttonLayout;
    }

    private TextField createNameField() {
        TextField clientName = new TextField("First Name");
        clientName.setPlaceholder("John");
        return clientName;

    }
    private TextField createLastNameField() {
        TextField clientName = new TextField("Last Name");
        clientName.setPlaceholder("Doe");
        return clientName;

    }


        private EmailField createEmailField() {
        EmailField emailCField = new EmailField("Email");
        emailCField.setPlaceholder("Example@gmail.com");
        return emailCField;

    }


    @Transactional
    public void saveClient(){
        try {
            Client client = createClient();
            clientRepository.persist(client);
            Utils.showInfoMessage("Client saved");
            clearFields();
        }catch (CustomException e){
            Utils.showErrorMessage(e.getMessage());
        }catch (Exception e) {
            Utils.showErrorMessage("An error occurred while trying to save the client");

        }

    }

    private void setPhoneNumberMask(){
        phoneField.setPlaceholder("+123-456-7890");

        phoneField.addValueChangeListener(event -> {
            String value = event.getValue();
            if (!value.matches("\\+?\\d{0,3}-?\\d{0,3}-?\\d{0,4}")) {
                phoneField.setValue(""); // Clear input if invalid
                phoneField.setErrorMessage("Invalid phone number format");
            }
        });

    }


    public void PhoneNumberMaskView() {

        // Set placeholder to show expected format
        phoneField.setPlaceholder("+123-456-7890");



        // Add a listener to apply the mask as the user types
        phoneField.addBlurListener( e -> {
            String rawValue = phoneField.getValue().replaceAll("[^0-9]", ""); // Remove all non-numeric characters
            StringBuilder maskedValue = new StringBuilder();

            // Apply the masking logic
            if (!rawValue.isEmpty()) {
                for (int i = 0; i < rawValue.length(); i++) {
                    if (i == 3 || i == 6) {
                        maskedValue.append("-");
                    }
                    maskedValue.append(rawValue.charAt(i));
                }
            }

            // Update the text field value with the masked format
            phoneField.setValue(maskedValue.toString());
        }); // Add debounce to prevent excessive calls

        // Add the phone field to the layout

    }




    private Client createClient() {
        Client client = new Client();
        client.setName(firstNameField.getValue());
        client.setLastName(lastNameField.getValue());
        client.setPhoneNumber(phoneField.getValue());
        client.setEmail(emailField.getValue());

        validateClient(client);

        return client;
    }

    private void validateClient(Client client){
        if(client.getName().isEmpty() || client.getLastName().isEmpty() || client.getPhoneNumber().isEmpty() || client.getEmail().isEmpty()){
            throw new CustomException("All fields are required");
        }
        if(!client.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")){
            throw new CustomException("Client email must be a valid email");

        }
    }

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        phoneField.clear();
        emailField.clear();
    }


}



