package com.apec.poo.view;

import com.apec.poo.entities.Client;
import com.apec.poo.entities.Product;
import com.apec.poo.repository.ClientRepository;
import com.apec.poo.repository.TransactionRepository;
import com.apec.poo.utils.ValidationMessage;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
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

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@PageTitle("All Clients")
@Route("allclients")
@ApplicationScoped
public class ClientGridView extends Composite<VerticalLayout> {

    private static final Logger LOGGER = Logger.getLogger(ClientGridView.class.getName());

    private static final String FULL_WIDTH = "100%";
    private static final String VAADIN = "vaadin";
    private static final String MAX_WIDTH = "100%";

    private static final String WIDTH = "140px";
    private static final String MIN_CONTENT = "min-content";
    private final ClientRepository clientRepository;
    private final TransactionRepository transactionRepository;

    private final Grid<Client> clientGrid;
    private final ValidationMessage firstNameValidationMessage = new ValidationMessage();
    private final ValidationMessage lastNameValidationMessage = new ValidationMessage();
    private final ValidationMessage emailValidationMessage = new ValidationMessage();


    @Inject
    public ClientGridView(ClientRepository clientRepository, TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        this.clientRepository = clientRepository;
        VerticalLayout mainLayout = createMainLayout();
        mainLayout.add(createDivForTitleandFilter());
        clientGrid = createClientGrid();
        mainLayout.add(clientGrid);
        getContent().add(mainLayout);
//        fillGridWithData();
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

    private HorizontalLayout createDivForTitleandFilter(){
        HorizontalLayout divLayout = new HorizontalLayout();
        divLayout.setWidthFull();
        divLayout.add(new H3("Product Information"), createFilterField());
        divLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        return divLayout;
    }

    private TextField createFilterField() {
        TextField filterField = new TextField();
        filterField.setPlaceholder("Filter...");
        filterField.setClearButtonVisible(true);
        filterField.setPrefixComponent(new Icon(VAADIN, "search"));
        filterField.addValueChangeListener(e -> filterClients(e.getValue()));
        return filterField;
    }

    private Grid<Client> createClientGrid() {
        Grid<Client> grid = new Grid<>(Client.class);
        Editor<Client> editor = grid.getEditor();
        grid.removeAllColumns();

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


        grid.addComponentColumn(client -> {
            Button deleteButton = new Button("Delete", new Icon(VAADIN, "trash"));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(event -> {
                ConfirmDialog dialog = new ConfirmDialog();
                dialog.setHeader("Delete Client");
                dialog.setText("Are you sure?");
                dialog.setCancelable(true);
                dialog.setConfirmText("Delete");
                dialog.setConfirmButtonTheme("error primary");
                dialog.addConfirmListener(eventDialog -> deleteClient(client));
                dialog.open();
                dialog.setVisible(true);
            });
            return deleteButton;
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


        Button updateButton = new Button("Save", new Icon(VAADIN, "check"), e -> {
            Client client = clientGrid.getEditor().getItem();
            updateClient(client, firstNameField, lastNameField, emailField, phoneField);
            editor.save();
        });


        updateButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        Button cancelButton = new Button(VaadinIcon.CLOSE.create(), e -> editor.cancel());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        HorizontalLayout actions = new HorizontalLayout(updateButton, cancelButton);
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

        grid.setWidthFull();

        return grid;
    }

    private void showErrorMessage(String message) {
        showMessage(message, NotificationVariant.LUMO_ERROR);
    }

    private void showInfoMessage(String message) {
        showMessage(message, NotificationVariant.LUMO_SUCCESS);
    }

    private void showWarningMessage(String message) {
        showMessage(message, NotificationVariant.LUMO_WARNING);
    }

    private void showMessage(String message, NotificationVariant variant) {
        Notification notification = new Notification(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(variant);
        notification.open();
    }

    @Transactional
    public void updateClient(Client client, TextField firstNameField, TextField lastNameField, EmailField emailField, TextField phoneField) {
        String clientNameUpdated = firstNameField.getValue();
        String lastNameUpdated = lastNameField.getValue();
        String emailUpdated = emailField.getValue();
        String phoneUpdated = phoneField.getValue();

        if (clientNameUpdated == null || clientNameUpdated.isEmpty()) {
            showErrorMessage("Client name must not be empty");
            return;
        }
        if (lastNameUpdated == null || lastNameUpdated.isEmpty()) {
            showErrorMessage("Client last name must not be empty");
            return;
        }
        if (emailUpdated == null || emailUpdated.isEmpty()) {
            showErrorMessage("Client email must not be empty");
            return;
        }
        if (phoneUpdated == null || phoneUpdated.isEmpty()) {
            showErrorMessage("Client phone number must not be empty");
            return;
        }

        clientRepository.updateClient(client, clientNameUpdated, lastNameUpdated, emailUpdated, phoneUpdated);
        showInfoMessage("Client updated successfully");
    }

    private void filterClients(String filterText) {
        List<Client> filteredClients;

        if (filterText == null || filterText.isEmpty()) {
            filteredClients = clientRepository.findAll().list();
        } else {
            List<Client> clientList = clientRepository.findAll().list();
            filteredClients = clientList.stream()
                    .filter(client -> client.getName().toLowerCase().contains(filterText.toLowerCase())
                            || client.getLastName().toLowerCase().contains(filterText.toLowerCase())
                            || client.getPhoneNumber().contains(filterText)
                            || client.getEmail().contains(filterText)).toList();
        }

        clientGrid.setItems(filteredClients);

    }


    public void fillGridWithData() {
        List<Client> clients = clientRepository.findAll().list();
        clientGrid.setItems(clients);

    }

    @Transactional
    public void deleteClient(Client client) {
        try {
            boolean isExistTransactionByClient = transactionRepository.getTransactionByClient(client.getId()).isPresent();
            if (isExistTransactionByClient) {
                showErrorMessage("You can not delete this client, because this has a transaction");
                return;
            }
            client = clientRepository.findById(client.getId());
            clientRepository.deleteClientById(client.getId());
            fillGridWithData();
            showInfoMessage("Client deleted successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error to try to delete the client", e);
            showErrorMessage("Error to try to delete the client");
        }

    }

}



