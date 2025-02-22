package com.skynet.javafx.controller;

import com.skynet.javafx.model.*;
import com.skynet.javafx.service.DevisService;
import com.skynet.javafx.service.CustomerService;
import com.skynet.javafx.service.ProductService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.context.annotation.Scope;
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import javafx.application.Platform;

@Controller
@Scope("prototype")
public class DevisController implements CrudController {
    
    @FXML private TextField numeroDevisField;
    @FXML private DatePicker dateDevisField;
    @FXML private ComboBox<Customer> clientComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ComboBox<Product> productComboBox;
    @FXML private VBox parametersContainer;
    @FXML private TextField quantityField;
    @FXML private TableView<DevisProduct> productsTable;
    @FXML private TableColumn<DevisProduct, String> productNameColumn;
    @FXML private TableColumn<DevisProduct, String> parametersColumn;
    @FXML private TableColumn<DevisProduct, Double> priceColumn;
    @FXML private TableColumn<DevisProduct, Integer> quantityColumn;
    @FXML private TableColumn<DevisProduct, Double> totalColumn;
    @FXML private TableColumn<DevisProduct, Button> actionsColumn;
    @FXML private Label totalLabel;
    @FXML private Pane buttonbar;
    @FXML private ButtonBarController buttonbarController;
    
    @Autowired private DevisService devisService;
    @Autowired private CustomerService customerService;
    @Autowired private ProductService productService;
    
    private Devis currentDevis;
    private ObservableList<DevisProduct> devisProducts;
    
    @FXML
    private void initialize() {
        try {
            setupComboBoxes();
            setupTable();
            initializeDevisProducts();
            
            Platform.runLater(() -> {
                try {
                    if (buttonbarController != null) {
                        buttonbarController.setTarget(this);
                    } else {
                        System.err.println("Warning: ButtonBarController not initialized");
                    }
                } catch (Exception e) {
                    System.err.println("Error initializing buttonbar: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("Error in initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupComboBoxes() {
        // Setup client combo box
        clientComboBox.setItems(FXCollections.observableArrayList(customerService.getAllCustomers()));
        clientComboBox.setCellFactory(param -> new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer customer, boolean empty) {
                super.updateItem(customer, empty);
                if (empty || customer == null) {
                    setText(null);
                } else {
                    setText(customer.getFirstname() + " " + customer.getLastname());
                }
            }
        });
        clientComboBox.setButtonCell(clientComboBox.getCellFactory().call(null));
        
        // Setup status combo box
        statusComboBox.setItems(FXCollections.observableArrayList("En cours", "Validé", "Annulé"));
        
        // Setup product combo box
        productComboBox.setItems(FXCollections.observableArrayList(productService.getData()));
        productComboBox.setCellFactory(param -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setText(null);
                } else {
                    setText(product.getName());
                }
            }
        });
        productComboBox.setButtonCell(productComboBox.getCellFactory().call(null));
    }
    
    private void setupTable() {
        productNameColumn.setCellValueFactory(cellData -> {
            DevisProduct dp = cellData.getValue();
            return dp.productNameProperty();
        });
        
        parametersColumn.setCellValueFactory(cellData -> {
            DevisProduct dp = cellData.getValue();
            return dp.parametersProperty();
        });
        
        priceColumn.setCellValueFactory(cellData -> {
            DevisProduct dp = cellData.getValue();
            return dp.priceProperty().asObject();
        });
        
        quantityColumn.setCellValueFactory(cellData -> {
            DevisProduct dp = cellData.getValue();
            return dp.quantityProperty().asObject();
        });
        
        totalColumn.setCellValueFactory(cellData -> {
            DevisProduct dp = cellData.getValue();
            return dp.totalProperty().asObject();
        });
        
        actionsColumn.setCellFactory(col -> new TableCell<DevisProduct, Button>() {
            private final Button deleteButton = new Button("Supprimer");
            {
                deleteButton.setOnAction(e -> {
                    DevisProduct product = getTableView().getItems().get(getIndex());
                    handleRemoveProduct(product);
                });
            }
            
            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });
    }
    
    private void initializeDevisProducts() {
        devisProducts = FXCollections.observableArrayList();
        productsTable.setItems(devisProducts);
    }
    
    @FXML
    private void handleAddProduct() {
        Product selectedProduct = productComboBox.getValue();
        if (selectedProduct != null && !quantityField.getText().isEmpty()) {
            try {
                int quantity = Integer.parseInt(quantityField.getText());
                DevisProduct devisProduct = new DevisProduct();
                devisProduct.setProduct(selectedProduct);
                devisProduct.setQuantity(quantity);
                devisProduct.setPrice(selectedProduct.getPrice());
                devisProduct.setDevis(currentDevis);
                
                devisProducts.add(devisProduct);
                updateTotal();
                clearProductInputs();
            } catch (NumberFormatException e) {
                showError("Quantité invalide");
            }
        }
    }
    
    private void handleRemoveProduct(DevisProduct product) {
        devisProducts.remove(product);
        updateTotal();
    }
    
    private void updateTotal() {
        double total = devisProducts.stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();
        totalLabel.setText(String.format("%.2f €", total));
        if (currentDevis != null) {
            currentDevis.setMontantTotal(total);
        }
    }
    
    @Override
    public void add() {
        currentDevis = new Devis();
        currentDevis.setDateDevis(LocalDateTime.now());
        currentDevis.setStatus("En cours");
        // The number will be generated when saved
        numeroDevisField.setText("(Auto-généré)");
        dateDevisField.setValue(LocalDateTime.now().toLocalDate());
        clientComboBox.setValue(null);
        statusComboBox.setValue("En cours");
        devisProducts.clear();
        updateTotal();
    }

    @Override
    public void render(SimpleEntity entity) {
        if (entity instanceof Devis) {
            currentDevis = (Devis) entity;
            numeroDevisField.setText(currentDevis.getNumeroDevis());
            dateDevisField.setValue(currentDevis.getDateDevis().toLocalDate());
            clientComboBox.setValue(currentDevis.getClient());
            statusComboBox.setValue(currentDevis.getStatus());
            devisProducts.setAll(currentDevis.getProducts());
            updateTotal();
        }
    }

    @Override
    public void save() {
        if (validateInputs()) {
            updateDevisFromInputs();
            devisService.save(currentDevis);
            // Update the displayed number after save
            numeroDevisField.setText(currentDevis.getNumeroDevis());
            showSuccess("Devis enregistré avec succès");
        }
    }
    
    private boolean validateInputs() {
        if (clientComboBox.getValue() == null) {
            showError("Veuillez sélectionner un client");
            return false;
        }
        if (statusComboBox.getValue() == null) {
            showError("Veuillez sélectionner un status");
            return false;
        }
        if (devisProducts.isEmpty()) {
            showError("Veuillez ajouter au moins un produit");
            return false;
        }
        return true;
    }
    
    private void updateDevisFromInputs() {
        currentDevis.setClient(clientComboBox.getValue());
        currentDevis.setDateDevis(LocalDateTime.now());
        currentDevis.setStatus(statusComboBox.getValue());
        currentDevis.getProducts().clear();
        currentDevis.getProducts().addAll(devisProducts);
    }
    
    private void clearProductInputs() {
        productComboBox.setValue(null);
        quantityField.clear();
        parametersContainer.getChildren().clear();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

