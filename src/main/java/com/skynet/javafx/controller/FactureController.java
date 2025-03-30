package com.skynet.javafx.controller;

import com.skynet.javafx.model.Facture;
import com.skynet.javafx.model.SimpleEntity;
import com.skynet.javafx.repository.FactureRepository;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.css.converter.StringConverter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.context.annotation.Scope;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.skynet.javafx.model.Product;
import com.skynet.javafx.model.FactureProduct;
import com.skynet.javafx.model.ParameterValue;
import com.skynet.javafx.repository.ProductRepository;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import com.skynet.javafx.repository.CustomerRepository;
import com.skynet.javafx.model.Customer;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import com.skynet.javafx.model.ProductParameter;
import javafx.scene.layout.VBox;

@Controller
@Scope("prototype")
public class FactureController implements CrudController, Initializable {

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @FXML
    private TextField numeroFactureField;
    @FXML
    private DatePicker dateFactureField;
    @FXML
    private ComboBox<Customer> clientComboBox;
    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private ComboBox<Product> productComboBox;
    @FXML
    private TextField quantityField;
    @FXML
    private TableView<FactureProduct> productsTable;
    @FXML
    private Label totalLabel;
    @FXML
    private TextArea commentField;
    @FXML
    private CheckBox isArchivedCheckBox;

    @FXML
    private TableColumn<FactureProduct, String> productNameColumn;
    @FXML
    private TableColumn<FactureProduct, String> parametersColumn;
    @FXML
    private TableColumn<FactureProduct, Double> priceColumn;
    @FXML
    private TableColumn<FactureProduct, Integer> quantityColumn;
    @FXML
    private TableColumn<FactureProduct, Double> totalColumn;
    @FXML
    private TableColumn<FactureProduct, Button> actionsColumn;

    @FXML
    private VBox parametersContainer;

    private Map<String, ComboBox<ParameterValue>> parameterFields = new HashMap<>();

    private Facture currentFacture;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupProductsTable();
        loadProducts();
        loadCustomers();
        statusComboBox.getItems().addAll("NOUVEAU", "EN_COURS", "PAYEE", "ANNULEE");
        totalLabel.setText("0.00 MAD");

        // Add quantity field validation
        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                quantityField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        productComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateParameterFields(newVal);
            }
        });
    }

    private void setupProductsTable() {
        productNameColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getProduct().getName()));
        parametersColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getParameterDescription()));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        totalColumn.setCellValueFactory(data -> 
            new SimpleObjectProperty<>(data.getValue().getTotal()));
        
        actionsColumn.setCellFactory(col -> new TableCell<FactureProduct, Button>() {
            private final Button deleteButton = new Button("Supprimer");
            {
                deleteButton.setOnAction(e -> {
                    FactureProduct fp = getTableView().getItems().get(getIndex());
                    currentFacture.removeProduct(fp);
                    updateProductsTable();
                });
            }
            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });
    }

    private void loadProducts() {
        ArrayList<Product> products = new ArrayList<Product>();
        productRepository.findAll().forEach(products::add);
        productComboBox.setItems(FXCollections.observableArrayList(products));
        productComboBox.setConverter(new javafx.util.StringConverter<Product>() { 
            @Override
            public String toString(Product product) {
                return product != null ? product.getName() : "";
            }
            @Override
            public Product fromString(String string) {
                return null;
            }
        });
    
    }

    private void loadCustomers() {
        ArrayList<Customer> customers = new ArrayList<Customer>();
        customerRepository.findAll().forEach(customers::add);
        clientComboBox.setItems(FXCollections.observableArrayList(customers));
        clientComboBox.setConverter(new javafx.util.StringConverter<Customer>() {
            @Override
            public String toString(Customer customer) {
                return customer != null ? customer.getFirstname() + " " + customer.getLastname() : "";
            }
            @Override
            public Customer fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    private void handleSave(ActionEvent event) {
        save();
        // Close the window
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleAdd() {
        clearAll();
    }

    @FXML
    private void handleAddProduct() {
        Product selectedProduct = productComboBox.getValue();
        if (selectedProduct == null) return;

        Map<String, ParameterValue> selectedParameters = new HashMap<>();
        for (Map.Entry<String, ComboBox<ParameterValue>> entry : parameterFields.entrySet()) {
            ParameterValue value = entry.getValue().getValue();
            if (value == null || value.getStockQuantity() <= 0) return;
            selectedParameters.put(entry.getKey(), value);
        }

        if (selectedProduct != null && !quantityField.getText().isEmpty()) {
            try {
                int quantity = Integer.parseInt(quantityField.getText());
                
                if (quantity <= 0) {
                    showError("La quantité doit être supérieure à 0");
                    return;
                }
                
                FactureProduct fp = new FactureProduct();
                fp.setProduct(selectedProduct);
                fp.setPrice(selectedProduct.getPrice());
                fp.setQuantity(quantity);
                currentFacture.addProduct(fp);
                updateProductsTable();
                clearProductFields();
            } catch (NumberFormatException e) {
                showError("La quantité doit être un nombre valide");
            }
        } else {
            showError("Veuillez sélectionner un produit, un paramètre et une quantité");
        }
    }

    private String generateInvoiceNumber() {
        String year = String.format("%02d", LocalDateTime.now().getYear() % 100);
        Facture lastFacture = factureRepository.findLastFactureForYear(year + "/");
        
        int nextNumber = 1;
        if (lastFacture != null) {
            String lastNumber = lastFacture.getNumeroFacture().split("/")[1];
            nextNumber = Integer.parseInt(lastNumber) + 1;
        }
        
        return String.format("%s/%02d", year, nextNumber);
    }

    @Override
    public void add() {
        currentFacture = new Facture();
        currentFacture.setDateFacture(LocalDateTime.now());
        currentFacture.setStatus("NOUVEAU");
        currentFacture.setNumeroFacture(generateInvoiceNumber());
        clearFields();
        updateProductsTable();
        updateFields(); // Add this to show the generated number
    }

    @Override
    public void render(SimpleEntity entity) {
        if (entity instanceof Facture) {
            this.currentFacture = (Facture) entity;
            updateFields();
            updateProductsTable();
        }
    }

    @Override
    public void save() {
        if (currentFacture != null) {
            updateFactureFromFields();
            factureRepository.save(currentFacture);
            clearFields();
        }
    }

    private void updateFields() {
        if (currentFacture != null) {
            numeroFactureField.setText(currentFacture.getNumeroFacture());
            dateFactureField.setValue(currentFacture.getDateFacture().toLocalDate());
            clientComboBox.setValue(currentFacture.getClient());
            statusComboBox.setValue(currentFacture.getStatus());
            commentField.setText(currentFacture.getComment());
            isArchivedCheckBox.setSelected(currentFacture.isArchived());
            updateProductsTable();
        }
    }

    private void updateFactureFromFields() {
        if (currentFacture != null) {
            currentFacture.setNumeroFacture(numeroFactureField.getText());
            currentFacture
                    .setDateFacture(LocalDateTime.of(dateFactureField.getValue(), LocalDateTime.now().toLocalTime()));
            currentFacture.setClient(clientComboBox.getValue());
            currentFacture.setStatus(statusComboBox.getValue());
            currentFacture.setComment(commentField.getText());
            currentFacture.setArchived(isArchivedCheckBox.isSelected());
            // montantTotal is calculated automatically when products are added/removed
        }
    }

    private void clearFields() {
        numeroFactureField.clear();
        dateFactureField.setValue(null);
        clientComboBox.setValue(null);
        statusComboBox.setValue(null);
        commentField.clear();
        isArchivedCheckBox.setSelected(false);
        if (currentFacture != null) {
            currentFacture.getProducts().clear();
            updateProductsTable();
        }
    }

    private void clearProductFields() {
        productComboBox.setValue(null);
        quantityField.clear();
    }

    private void updateProductsTable() {
        productsTable.getItems().setAll(currentFacture.getProducts());
        totalLabel.setText(String.format("%.2f MAD", currentFacture.getMontantTotal()));
    }

    private void clearAll() {
        currentFacture = new Facture();
        clearFields();
        clearProductFields();
        updateProductsTable();
        totalLabel.setText("0.00 MAD");
    }

    private void updateParameterFields(Product product) {
        parametersContainer.getChildren().clear();
        parameterFields.clear();

        for (ProductParameter param : product.getParameters()) {
            ComboBox<ParameterValue> paramField = new ComboBox<>();
            paramField.setPromptText(param.getName());
            paramField.setPrefWidth(150);
            
            paramField.setItems(FXCollections.observableArrayList(param.getValues()));
            
            // Disable values with zero stock
            paramField.setCellFactory(lv -> new ListCell<ParameterValue>() {
                @Override
                protected void updateItem(ParameterValue item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getValue() + " (Stock: " + item.getStockQuantity() + ")");
                        setDisable(item.getStockQuantity() <= 0);
                    }
                }
            });
            
            parameterFields.put(param.getName(), paramField);
            parametersContainer.getChildren().add(paramField);
        }
    }

    // Additional helper methods
    public Facture getCurrentFacture() {
        return currentFacture;
    }

    public void setCurrentFacture(Facture facture) {
        this.currentFacture = facture;
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
