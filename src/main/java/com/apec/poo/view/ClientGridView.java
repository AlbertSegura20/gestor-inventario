package com.apec.poo.view;
import com.apec.poo.entities.Client;
import com.apec.poo.repository.ClientRepository;
import com.apec.poo.utils.ValidationMessage;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;


@PageTitle("All Clients")
@Route("allclients")
@ApplicationScoped
public class ClientGridView extends Composite<VerticalLayout> {


    private final ClientRepository clientRepository;
    private final ClientGridView self;
    private static final String FULL_WIDTH = "100%";
    private static final String VAADIN = "vaadin";;
    private static final String MAX_WIDTH = "100%";
    private static final String WIDTH = "140px";
    private static final String MIN_CONTENT = "min-content";
    private final Grid<Client> clientGrid; // The grid listing the clients.
//    private Button editButton; // Button to trigger edit/save of client.
    private final ValidationMessage firstNameValidationMessage = new ValidationMessage();
    private final ValidationMessage lastNameValidationMessage = new ValidationMessage();
    private final ValidationMessage emailValidationMessage = new ValidationMessage();


    @Inject
    public ClientGridView(ClientRepository clientRepository, ClientGridView self) {
        this.clientRepository = clientRepository;
        VerticalLayout mainLayout = createMainLayout();
        // Add a title
        mainLayout.add(new H3("Client Information"));
        // Add Grid
        clientGrid = createClientGrid();
        mainLayout.add(clientGrid);
        getContent().add(mainLayout);
        fillGridWithData();
        this.self = self;
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


    private Grid<Client> createClientGrid() {
        // Create a grid for the Client entity
        Grid<Client> grid = new Grid<>(Client.class);
        Editor<Client> editor = grid.getEditor();


        // Set the columns to display specific properties of the Client entity
        grid.removeAllColumns(); // Clear auto-generated columns


        Grid.Column<Client> firstNameColumn = grid
                .addColumn(Client::getName).setHeader("First name")
                .setWidth(WIDTH)
                .setFlexGrow(0);
        Grid.Column<Client> lastNameColumn = grid
                .addColumn(Client::getLastName)
                .setHeader("Last name")
                .setWidth(WIDTH)
                .setFlexGrow(0);
        Grid.Column<Client> emailColumn = grid
                .addColumn(Client::getEmail)
                .setWidth(WIDTH)
                .setHeader("Email");
        Grid.Column<Client> phoneColumn = grid
                .addColumn(Client::getPhoneNumber)
                .setWidth(WIDTH)
                .setHeader("Phone");

        Grid.Column<Client> editColumn = grid.addComponentColumn(client -> {
            Button editButton = new Button("Edit", new Icon(VAADIN, "edit"));
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.getEditor().editItem(client);
            });
            return editButton;
        }).setWidth("160px").setFlexGrow(0);


        Grid.Column<Client> deleteColumn = grid.addComponentColumn(client -> {
            Button deleteButton = new Button("Delete", new Icon(VAADIN, "trash"));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(event -> {
                deleteButtonClicked(client);
                fillGridWithData();
            }); return deleteButton;
        }).setWidth(WIDTH).setFlexGrow(0);


        Binder<Client> binder = new Binder<>(Client.class);
        editor.setBinder(binder);
        editor.setBuffered(true);

        TextField firstNameField = new TextField();
        firstNameField.setWidthFull();
        binder.forField(firstNameField)
                .asRequired("First name must not be empty")
                .withStatusLabel(firstNameValidationMessage)
                .bind(Client::getName, Client::setName);
        firstNameColumn.setEditorComponent(firstNameField);

        TextField lastNameField = new TextField();
        lastNameField.setWidthFull();
        binder.forField(lastNameField).asRequired("Last name must not be empty")
                .withStatusLabel(lastNameValidationMessage)
                .bind(Client::getLastName, Client::setLastName);
        lastNameColumn.setEditorComponent(lastNameField);

        EmailField emailField = new EmailField();
        emailField.setWidthFull();
        binder.forField(emailField).asRequired("Email must not be empty")
                .withValidator(
                        new EmailValidator("Enter a valid email address"))
                .withStatusLabel(emailValidationMessage)
                .bind(Client::getEmail, Client::setEmail);
        emailColumn.setEditorComponent(emailField);

        TextField phoneField = new TextField();
        phoneField.setWidthFull();
        binder.forField(phoneField)
                .asRequired("Phone number must not be empty")
                .withStatusLabel(firstNameValidationMessage)
                .bind(Client::getPhoneNumber, Client::setPhoneNumber);
        phoneColumn.setEditorComponent(phoneField);


        Button saveButton = new Button("Save", new Icon(VAADIN, "check"), e -> {
            editButtonClicked(clientGrid.getEditor().getItem(), firstNameField, lastNameField, emailField, phoneField);
            editor.save();
        });


        saveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        Button cancelButton = new Button(VaadinIcon.CLOSE.create(),
                e -> editor.cancel());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_ERROR);
        HorizontalLayout actions = new HorizontalLayout(saveButton,
                cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);


        editor.addCancelListener(e -> {
            firstNameValidationMessage.setText("");
            lastNameValidationMessage.setText("");
            emailValidationMessage.setText("");
        });

        actions.getThemeList().clear();
        actions.getThemeList().add("spacing-s");
        actions.add(grid, firstNameValidationMessage, lastNameValidationMessage, emailValidationMessage);

        // Set the width of the grid to full
        grid.setWidthFull();

        return grid;
    }



    public void fillGridWithData(){
        List<Client> clients = clientRepository.findAll().list();
        clientGrid.setItems(clients);
    }


    private void deleteButtonClicked(Client client) {
        self.deleteClient(client);
    }

    @Transactional
    public void deleteClient(Client client) {
        try {
            clientRepository.delete(client);
        } catch (Exception e) {
            Notification notification = new Notification("Error al eliminar el cliente", 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();

        }

    }

    private void editButtonClicked(Client client, TextField firstNameField, TextField lastNameField, EmailField emailField, TextField phoneField ) {
        // Call the transactional method via the proxy
        self.updateClient(client, firstNameField, lastNameField, emailField, phoneField);
    }

    @Transactional
    public void updateClient(Client client, TextField firstNameField, TextField lastNameField, EmailField emailField, TextField phoneField) {
        // Validate fields
        String clientNameUpdated = firstNameField.getValue();
        String lastNameUpdated = lastNameField.getValue();
        String emailUpdated = emailField.getValue();
        String phoneUpdated = phoneField.getValue();

        if (clientNameUpdated == null || clientNameUpdated.isEmpty() ||
                lastNameUpdated == null || lastNameUpdated.isEmpty() ||
                emailUpdated == null || emailUpdated.isEmpty() ||
                phoneUpdated == null || phoneUpdated.isEmpty()){
            Notification notification = new Notification("Todos los campos deben estar llenos.", 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return;
        }

        try {
            // Manually modify the entity fields
            Client clientToUpdate = clientRepository.findById(client.getId());
            if (clientToUpdate != null) {
                clientToUpdate.setName(clientNameUpdated);
                clientToUpdate.setLastName(lastNameUpdated);
                clientToUpdate.setEmail(emailUpdated);
                clientToUpdate.setPhoneNumber(phoneUpdated);
            }

            // JPA will automatically detect changes and persist them when the transaction completes
            fillGridWithData(); // Refresh the client grid
        } catch (Exception e) {
            Notification notification = new Notification("Error al actualizar el cliente", 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();

        }
    }



}



