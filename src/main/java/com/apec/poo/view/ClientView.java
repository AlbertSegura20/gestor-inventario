package com.apec.poo.view;

import com.apec.poo.entities.Client;
import com.apec.poo.repository.ClientRepository;
import com.apec.poo.repository.ProductRepository;
import com.apec.poo.utils.CustomException;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.component.tabs.Tabs;
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
    TextField firstNameField = new TextField("First Name");
    TextField lastNameField = new TextField("Last Name");
    TextField phoneField = new TextField("Phone Number");
    EmailField emailField = new EmailField("Email");
    private static final String FULL_WIDTH = "100%";
    private static final String MAX_WIDTH = "800px";
    private static final String MIN_CONTENT = "min-content";
    Tab tab1 = new Tab("Register client");
    Tab tab2 = new Tab("All clients");
    TabSheet tabs = new TabSheet();

    @Inject
    public ClientView(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
        VerticalLayout mainLayout = createMainLayout();
        VerticalLayout content = new VerticalLayout();
        FormLayout formLayout = createFormLayout();
        HorizontalLayout buttonLayout = saveButtonLayout();
        tabs.setWidth(FULL_WIDTH);
        tabs.add(tab1, mainLayout);
        tabs.add(tab2, new ClientGridView(clientRepository));

        content.add(tabs);
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
            Notification notification = Notification.show("Operacion cancelada", 3000, Notification.Position.BOTTOM_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        });

        cancelButton.setWidth(MIN_CONTENT);

        buttonLayout.add(saveButton, cancelButton);
        return buttonLayout;
    }


    @Transactional
    public void saveClient(){
        try {
            Client client = createClient();
            clientRepository.persist(client);
            Notification notification = Notification.show("Cliente guardado", 3000, Notification.Position.BOTTOM_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            firstNameField.clear();
            lastNameField.clear();
            phoneField.clear();
            emailField.clear();
        }catch (CustomException e){
            Notification notification = Notification.show(e.getMessage(), 3000, Notification.Position.BOTTOM_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }catch (Exception e) {
            Notification notification = Notification.show("Se produjo un error intentando guardar al cliente", 3000, Notification.Position.BOTTOM_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }

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
            throw new CustomException("Todos los campos son requeridos");
        }
    }

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        phoneField.clear();
        emailField.clear();
    }

}



