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
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;


@PageTitle("All Clients")
@Route("allclients")
@Menu(order = 3, title = "All Clients")
@ApplicationScoped
public class ClientGridView extends Composite<VerticalLayout> {


    private final ClientRepository clientRepository;
    private static final String FULL_WIDTH = "100%";
    private static final String MAX_WIDTH = "100%";
    private static final String WIDTH = "160px";
    private static final String MIN_CONTENT = "min-content";
    private final Grid<Client> clientGrid; // The grid listing the clients.
//    private Button editButton; // Button to trigger edit/save of client.
    private final ValidationMessage firstNameValidationMessage = new ValidationMessage();
    private final ValidationMessage lastNameValidationMessage = new ValidationMessage();
    private final ValidationMessage emailValidationMessage = new ValidationMessage();

    @Inject
    public ClientGridView( ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
        VerticalLayout mainLayout = createMainLayout();

        // Add a title
        mainLayout.add(new H3("Client Informations"));
        // Add Grid
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

        Grid.Column<Client> editColumn = grid.addComponentColumn(client -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.getEditor().editItem(client);
            });
            return editButton;
        }).setWidth(WIDTH).setFlexGrow(0);

        Grid.Column<Client> deleteRow = grid.addComponentColumn(client -> {
            Button editButton = new Button("Delete");
            editButton.addClickListener(e -> {
               // Borrar cliente, pending action
            });
            return editButton;
        }).setWidth("100px").setFlexGrow(0);



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

        Button saveButton = new Button("Save", e -> editor.save());
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

    @PostConstruct
    private void fillGridWithData(){
        List<Client> clients = clientRepository.findAll().list();
        clientGrid.setItems(clients);
    }

}



