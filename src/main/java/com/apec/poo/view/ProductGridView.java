package com.apec.poo.view;
import com.apec.poo.entities.Client;
import com.apec.poo.entities.Product;
import com.apec.poo.entities.ProductStatus;
import com.apec.poo.repository.ClientRepository;
import com.apec.poo.repository.ProductRepository;
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
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@PageTitle("All Products")
@Route("allproducts")
@Menu(order = 4, title = "All Products")
public class ProductGridView extends Composite<VerticalLayout> {

    @Inject
    ClientRepository clientRepository;

    private static final String FULL_WIDTH = "100%";
    private static final String MAX_WIDTH = "100%";
    private static final String MIN_CONTENT = "min-content";


    private final Grid<Product> productGrid; // The grid listing the clients.
    @Inject
    ProductRepository productRepository;
    private Button editButton; // Button to trigger edit/save of client.
    private final ValidationMessage firstNameValidationMessage = new ValidationMessage();
    private final ValidationMessage lastNameValidationMessage = new ValidationMessage();
    private final ValidationMessage emailValidationMessage = new ValidationMessage();

    public ProductGridView() {
        VerticalLayout mainLayout = createMainLayout();

        // Add a title
        mainLayout.add(new H3("Product Informations"));

        // Add Grid
        productGrid = createProductGrid();
        mainLayout.add(productGrid);
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

    private Grid<Product> createProductGrid() {
        // Create a grid for the Client entity
        Grid<Product> grid = new Grid<>(Product.class);
        Editor<Product> editor = grid.getEditor();


        // Set the columns to display specific properties of the Client entity
        grid.removeAllColumns(); // Clear auto-generated columns


        Grid.Column<Product> idColumn = grid
                .addColumn(Product::getId).setHeader("ID")
                .setWidth("90px")
                .setFlexGrow(0);
        Grid.Column<Product> nameColumn = grid
                .addColumn(Product::getName).setHeader("Name")
                .setWidth("120px")
                .setFlexGrow(0);
        Grid.Column<Product> descriptionColumn = grid
                .addColumn(Product::getDescription)
                .setHeader("Description")
                .setWidth("160px")
                .setFlexGrow(0);
        Grid.Column<Product> priceColumn = grid
                .addColumn(Product::getPrice)
                .setWidth("80px")
                .setHeader("Price");
        Grid.Column<Product> statusColumn = grid
                .addColumn(Product::getStatus)
                .setWidth("120px")
                .setHeader("Status");
        Grid.Column<Product> dateColumn = grid
                .addColumn(Product::getRegistryDate)
                .setWidth("120px")
                .setHeader("Registry Date");
        Grid.Column<Product> quantityColumn = grid
                .addColumn(Product::getQuantity)
                .setWidth("90px")
                .setHeader("Quantity");

        Grid.Column<Product> editColumn = grid.addComponentColumn(product -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.getEditor().editItem(product);
            });
            return editButton;
        }).setWidth("130px").setFlexGrow(0);

        Grid.Column<Product> deleteRow = grid.addComponentColumn(product -> {
            Button editButton = new Button("Delete");
            editButton.addClickListener(e -> {
                // Borrar cliente, pending action
            });
            return editButton;
        }).setWidth("100px").setFlexGrow(0);



        Binder<Product> binder = new Binder<>(Product.class);
        editor.setBinder(binder);
        editor.setBuffered(true);

        TextField nameField = new TextField();
        nameField.setWidthFull();
        binder.forField(nameField)
                .asRequired("Name must not be empty")
                .withStatusLabel(firstNameValidationMessage)
                .bind(Product::getName, Product::setName);
        nameColumn.setEditorComponent(nameField);

        TextField descriptioonField = new TextField();
        descriptioonField.setWidthFull();
        binder.forField(descriptioonField).asRequired("Description must not be empty")
                .withStatusLabel(lastNameValidationMessage)
                .bind(Product::getDescription, Product::setDescription);
        descriptionColumn.setEditorComponent(descriptioonField);

        TextField priceField = new TextField();
        priceField.setWidthFull();
        binder.forField(priceField).asRequired("Price must not be empty")
                .withValidator(
                        new EmailValidator("Enter a valid email address"))
                .withStatusLabel(emailValidationMessage)
                .withConverter(new StringToBigDecimalConverter("Invalid price format"))
                .bind(Product::getPrice, Product::setPrice);
        priceColumn.setEditorComponent(priceField);

        //resto de propiedades pendientes



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
        List<Product> products = productRepository.findAll().list();
        productGrid.setItems(products);
    }
}



