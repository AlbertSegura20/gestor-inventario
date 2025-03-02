package com.apec.poo.view;

import com.apec.poo.entities.Product;
import com.apec.poo.repository.ProductRepository;
import com.apec.poo.repository.TransactionRepository;
import com.apec.poo.utils.Utils;
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
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;


@PageTitle("All Products")
@Route("allproducts")
public class ProductGridView extends Composite<VerticalLayout> {


    private static final String FULL_WIDTH = "100%";
    private static final String MAX_WIDTH = "100%";
    private static final String MIN_CONTENT = "min-content";
    private static final String WIDTH_130 = "130px";
    private final Grid<Product> productGrid;
    private static final String VAADIN = "vaadin";
    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private final ValidationMessage firstNameValidationMessage = new ValidationMessage();
    private final ValidationMessage lastNameValidationMessage = new ValidationMessage();
    private final ValidationMessage priceValidationMessage = new ValidationMessage();


    @Inject
    public ProductGridView(ProductRepository productRepository, TransactionRepository transactionRepository) {
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
        VerticalLayout mainLayout = createMainLayout();

        // Add a title
        mainLayout.add(createDivForTitleandFilter());

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
        filterField.addValueChangeListener(e -> filterProducts(e.getValue()));
        return filterField;
    }


    private Grid<Product> createProductGrid() {
        Grid<Product> grid = new Grid<>(Product.class);
        Editor<Product> editor = grid.getEditor();
        grid.removeAllColumns();

        Grid.Column<Product> nameColumn = grid
                .addColumn(Product::getName).setHeader("Name")
                .setWidth(WIDTH_130)
                .setFlexGrow(0);
        Grid.Column<Product> descriptionColumn = grid
                .addColumn(Product::getDescription)
                .setHeader("Description")
                .setWidth("200px")
                .setFlexGrow(0);
        Grid.Column<Product> priceColumn = grid
                .addColumn(Product::getTransientPrice)
                .setWidth("100px")
                .setHeader("Price");

        grid.addColumn(Product::getStatus)
                .setWidth(WIDTH_130)
                .setHeader("Status");

        grid.addColumn(Product::getRegistryDate)
                .setWidth(WIDTH_130)
                .setHeader("Registry date");

        Grid.Column<Product> quantityColumn = grid
                .addColumn(Product::getQuantity)
                .setWidth("90px")
                .setHeader("Quantity");

        Grid.Column<Product> editColumn = grid.addComponentColumn(product -> {
            Button editButton = new Button("Edit", new Icon(VAADIN, "edit"));
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.getEditor().editItem(product);
            });
            return editButton;
        }).setWidth("150px").setFlexGrow(0);

        grid.addComponentColumn(product -> {
            Button deleteButton = new Button("Delete", new Icon(VAADIN, "trash"));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> {


                ConfirmDialog dialog = new ConfirmDialog();
                dialog.setHeader("Delete Product");
                dialog.setText("Are you sure?");
                dialog.setCancelable(true);
                dialog.setConfirmText("Delete");
                dialog.setConfirmButtonTheme("error primary");
                dialog.addConfirmListener(eventDialog -> deleteProduct(product));
                dialog.open();
                dialog.setVisible(true);
            });
            return deleteButton;
        }).setWidth("150px").setFlexGrow(0);


        Binder<Product> binder = new Binder<>(Product.class);
        editor.setBinder(binder);
        editor.setBuffered(true);

        TextField nameField = new TextField();
        nameField.setWidthFull();
        binder.forField(nameField)
                .asRequired("Name must not be empty")

                .bind(Product::getName, Product::setName);
        nameColumn.setEditorComponent(nameField);

        TextField descriptionField = new TextField();
        descriptionField.setWidthFull();
        binder.forField(descriptionField).asRequired("Description must not be empty")

                .bind(Product::getDescription, Product::setDescription);
        descriptionColumn.setEditorComponent(descriptionField);

        TextField priceField = new TextField();
        priceField.setWidthFull();
        binder.forField(priceField).asRequired("Price must not be empty")
                .bind(Product::getTransientPrice, Product::setTransientPrice);
        priceColumn.setEditorComponent(priceField);

        TextField quantity = new TextField();
        quantity.setWidthFull();
        binder.forField(quantity).asRequired("Quantity must not be empty")
                .withConverter(new StringToIntegerConverter("Invalid quantity format"))
                .bind(Product::getQuantity, Product::setQuantity);
        quantityColumn.setEditorComponent(quantity);

        Button updateButton = new Button("Save", new Icon(VAADIN, "check"), e -> {
            Product product = grid.getEditor().getItem();
            editor.save();
            updateProduct(product, nameField, descriptionField, priceField, quantity);
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
            priceValidationMessage.setText("");
        });

        actions.getThemeList().clear();
        actions.getThemeList().add("spacing-s");
        actions.add(grid, firstNameValidationMessage, lastNameValidationMessage, priceValidationMessage);


        // Set the width of the grid to full
        grid.setWidthFull();

        return grid;
    }

    public void updateProduct(Product product, TextField nameField, TextField descriptionField, TextField priceField, TextField quantityField) {
        String nameUpdated = nameField.getValue();
        String descriptionUpdated = descriptionField.getValue();
        String priceUpdated = priceField.getValue();
        String quantityUpdated = quantityField.getValue();

        if (nameUpdated == null || nameUpdated.isEmpty()) {
            Utils.showErrorMessage("Name must not be empty");
            return;
        }
        if (descriptionUpdated == null || descriptionUpdated.isEmpty()) {
            Utils.showErrorMessage("Description must not be empty");
            return;
        }
        if (priceUpdated == null || priceUpdated.isEmpty()) {
            Utils.showErrorMessage("Price must not be empty");
            return;
        }
        if (quantityUpdated == null || quantityUpdated.isEmpty()) {
            Utils.showErrorMessage("Quantity must not be empty");
            return;
        }
        BigDecimal price;
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        decimalFormat.setParseBigDecimal(true);
        try {
            price = (BigDecimal) decimalFormat.parse(priceUpdated);
        } catch (ParseException e) {
            Utils.showErrorMessage("Invalid price format. Please enter a valid number.");
            return;
        }

        int quantity = Integer.parseInt(quantityUpdated);
        productRepository.updateProduct(product.getId(), nameUpdated, price, quantity, descriptionUpdated);
        Utils.showInfoMessage("The product has been updated successfully");

    }

    private void deleteProduct(Product product) {
        boolean isExistTransaction = transactionRepository.existsTransactionByProduct(product.getId());
        if (isExistTransaction) {
            Utils.showErrorMessage("The product cannot be deleted. Transaction exits");
            return;
        }
        try {
            productRepository.deleteProductById(product.getId());
            Utils.showInfoMessage("The product has been deleted successfully");
            fillGridWithData();
        } catch (Exception e) {
            Utils.showErrorMessage("Error deleting the product");
        }

    }

    private void filterProducts(String filterText) {

        List<Product> filteredProducts;

        if (filterText == null || filterText.isEmpty()) {
            filteredProducts = productRepository.findAll().list();
        } else {
            List<Product> productsList = productRepository.findAll().list();
            filteredProducts = productsList.stream()
                    .filter(product -> product.getName().toLowerCase().contains(filterText.toLowerCase())
                            || product.getDescription().toLowerCase().contains(filterText.toLowerCase())
                            || product.getPrice().toString().contains(filterText)
                            || product.getStatus().toString().contains(filterText)
                            || product.getRegistryDate().toString().contains(filterText)).toList();
        }

        filteredProducts.forEach(Product::loadPrice);
        productGrid.setItems(filteredProducts);

    }


    public void fillGridWithData() {
        List<Product> products = productRepository.findAll().list();
        products.forEach(Product::loadPrice);
        productGrid.setItems(products);
    }

}



