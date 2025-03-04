package com.apec.poo.view;

import com.apec.poo.entities.AbstractEntity;
import com.apec.poo.entities.Client;
import com.apec.poo.repository.ClientRepository;
import com.apec.poo.repository.TransactionRepository;
import com.apec.poo.utils.Utils;
import com.apec.poo.utils.ValidationMessage;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


@PageTitle("All Clients")
@Route("allclients")
@ApplicationScoped
public class ClientGridView extends ClientViewAbstract {

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

    private HorizontalLayout createDivForTitleandFilter() {
        HorizontalLayout divLayout = new HorizontalLayout();
        divLayout.setWidthFull();
        divLayout.add(new H3("Customer List"), createFilterField());
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
                .setWidth("200px")
                .setFlexGrow(0);
        Grid.Column<Client> lastNameColumn = grid
                .addColumn(Client::getLastName)
                .setHeader("Last name")
                .setWidth("200px")
                .setFlexGrow(0);
        Grid.Column<Client> emailColumn = grid
                .addColumn(Client::getEmail)
                .setWidth("200px")
                .setHeader("Email");
        Grid.Column<Client> codeColumn = grid
                .addColumn(Client::getCountryCode)
                .setWidth("150px")
                .setHeader("Code");
        Grid.Column<Client> phoneColumn = grid
                .addColumn(Client::getPhoneNumber)
                .setWidth("154px")
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
        firstNameField.setRequiredIndicatorVisible(true);
        firstNameField.addValueChangeListener(event -> nameFieldValidationListener(event, firstNameField));
        binder.forField(firstNameField)
                .asRequired("First name must not be empty")
                .withStatusLabel(firstNameValidationMessage)
                .bind(Client::getName, Client::setName);
        firstNameColumn.setEditorComponent(firstNameField);

        TextField lastNameField = new TextField();
        lastNameField.setWidthFull();
        lastNameField.setRequiredIndicatorVisible(true);
        lastNameField.addValueChangeListener(event -> lastNameFieldValidationListener(event, lastNameField));
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

        ComboBox<String> codeField = createCountryCodeField();
        codeField.setLabel("");
        codeField.setWidthFull();
        binder.forField(codeField)
                .asRequired("Country Code must not be empty")
                .withStatusLabel(firstNameValidationMessage)
                .bind(Client::getCountryCode, Client::setCountryCode);
        codeColumn.setEditorComponent(codeField);


        TextField phoneField = new TextField();
        phoneField.setWidthFull();
        phoneField.addValueChangeListener(event -> phoneFieldValidationListener(event, phoneField));
        binder.forField(phoneField)
                .asRequired("Phone number must not be empty")
                .withStatusLabel(firstNameValidationMessage)
                .bind(Client::getPhoneNumber, Client::setPhoneNumber);
        phoneColumn.setEditorComponent(phoneField);


        Button updateButton = new Button("Save", new Icon(VAADIN, "check"), e -> {
            Client client = clientGrid.getEditor().getItem();
            Map<String, Boolean> fields = new HashMap<>();
            fields.put("First Name", firstNameField.isInvalid());
            fields.put("Last Name", lastNameField.isInvalid());
            fields.put("Email", emailField.isInvalid());
            fields.put("Phone", phoneField.isInvalid());
            fields.put("Code", codeField.isInvalid());

            for (Map.Entry<String, Boolean> entry : fields.entrySet()) {
                if(entry.getValue()){
                    Utils.showErrorMessage(entry.getKey() + " is invalid");
                    return;
                }
            }

            boolean isSuccess = updateClient(client, firstNameField, lastNameField, emailField, phoneField, codeField);
            if (isSuccess) {
                editor.save();
            }
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


    @Transactional
    public boolean updateClient(Client client, TextField firstNameField, TextField lastNameField, EmailField emailField,
                                TextField phoneField, ComboBox<String> countryCodeField) {
        String clientNameUpdated = firstNameField.getValue();
        String lastNameUpdated = lastNameField.getValue();
        String emailUpdated = emailField.getValue();
        String phoneUpdated = phoneField.getValue();
        String countryCodeUpdated = countryCodeField.getValue();

        if (clientNameUpdated == null || clientNameUpdated.isEmpty()) {
            Utils.showErrorMessage("Client name must not be empty");
            return false;
        }
        if (lastNameUpdated == null || lastNameUpdated.isEmpty()) {
            Utils.showErrorMessage("Client last name must not be empty");
            return false;
        }
        if (emailUpdated == null || emailUpdated.isEmpty()) {
            Utils.showErrorMessage("Client email must not be empty");
            return false;
        }
        if (phoneUpdated == null || phoneUpdated.isEmpty()) {
            Utils.showErrorMessage("Client phone number must not be empty");
            return false;
        }

        if (countryCodeUpdated == null || countryCodeUpdated.isEmpty()) {
            Utils.showErrorMessage("Country code must not be empty");
            return false;
        }

        if (!emailUpdated.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            Utils.showErrorMessage("Client email must be a valid email");
            return false;
        }

        try {
            clientRepository.updateClient(client, clientNameUpdated, lastNameUpdated, emailUpdated, phoneUpdated, countryCodeUpdated);
        } catch (Exception e) {
            Utils.showErrorMessage("Error updating client, please try again later");
            return false;
        }

        Utils.showInfoMessage("Client updated successfully");
        return true;
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
                            || client.getEmail().contains(filterText)
                            || client.getCountryCode().contains(filterText)
                    ).toList();
        }

        clientGrid.setItems(filteredClients);

    }


    public void fillGridWithData() {
        List<Client> clients = clientRepository.findAll().list();

        clients.sort(Comparator.comparing(Client::getId));
        clientGrid.setItems(clients);

    }

    @Transactional
    public void deleteClient(Client client) {
        try {
            boolean isExistTransactionByClient = transactionRepository.getTransactionByClient(client.getId()).isPresent();
            if (isExistTransactionByClient) {
                Utils.showErrorMessage("You can not delete this client, because this has a transaction");
                return;
            }
            client = clientRepository.findById(client.getId());
            clientRepository.deleteClientById(client.getId());
            fillGridWithData();
            Utils.showInfoMessage("Client deleted successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error trying to delete the client", e);
            Utils.showErrorMessage("Error trying delete the client");
        }

    }

}