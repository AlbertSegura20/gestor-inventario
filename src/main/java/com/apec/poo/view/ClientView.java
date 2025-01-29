package com.apec.poo.view;

import com.apec.poo.entities.Client;
import com.apec.poo.repository.ClientRepository;
import com.apec.poo.utils.CustomException;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
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



@PageTitle("Person Form")
@Route("client")
@Menu(order = 0, title = "Client")
public class ClientView extends Composite<VerticalLayout> {

    @Inject
    ClientRepository clientRepository;
    TextField firstNameField = new TextField("First Name");
    TextField lastNameField = new TextField("Last Name");
    TextField phoneField = new TextField("Phone Number");
    EmailField emailField = new EmailField("Email");
    private static final String FULL_WIDTH = "100%";
    private static final String MAX_WIDTH = "800px";
    private static final String MIN_CONTENT = "min-content";

    public ClientView() {
        VerticalLayout mainLayout = createMainLayout();
        FormLayout formLayout = createFormLayout();
        HorizontalLayout buttonLayout = saveButtonLayout();

        mainLayout.add(new H3("Client Information"), formLayout, buttonLayout);
        getContent().add(mainLayout);
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
        cancelButton.setWidth(MIN_CONTENT);

        buttonLayout.add(saveButton, cancelButton);
        return buttonLayout;
    }


    @Transactional
    public void saveClient(){
        try {
            Client client = createClient();
            clientRepository.persist(client);
            Notification.show("Saved", 3000, Notification.Position.BOTTOM_CENTER);
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

        if(client.getName().isEmpty() && client.getLastName().isEmpty() && client.getPhoneNumber().isEmpty()
                && client.getEmail().isEmpty()) {
            throw new CustomException("All fields cannot be empty");
        }

        if (client.getName().isEmpty()) {
            throw new CustomException("Name cannot be empty");
        }

        if (client.getLastName().isEmpty()) {
            throw new CustomException("Last Name cannot be empty");
        }

        if (client.getPhoneNumber().isEmpty()) {
            throw new CustomException("Phone Number cannot be empty");
        }
        if (client.getEmail().isEmpty()) {
            throw new CustomException("Email cannot be empty");
        }

        return client;
    }

}



