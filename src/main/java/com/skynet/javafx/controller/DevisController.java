package com.skynet.javafx.controller;

import com.itextpdf.layout.element.Text;
import com.skynet.javafx.model.*;
import com.skynet.javafx.repository.DevisRepository;
import com.skynet.javafx.service.DevisService;
import com.skynet.javafx.service.CustomerService;
import com.skynet.javafx.service.ProductService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.context.annotation.Scope;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.layout.Pane;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.ListView;
import javafx.stage.StageStyle;
import javafx.geometry.Bounds;
import java.util.stream.Collectors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.List;

@Controller
@Scope("prototype")
public class DevisController implements CrudController {

    @FXML
    private TextField numeroDevisField;
    @FXML
    private DatePicker dateDevisField;
    @FXML
    private ComboBox<Customer> clientComboBox;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private ComboBox<Product> productComboBox;
    @FXML
    private VBox parametersContainer;
    @FXML
    private TextField quantityField;
    @FXML
    private TableView<DevisProduct> productsTable;
    @FXML
    private TableColumn<DevisProduct, String> productNameColumn;
    @FXML
    private TableColumn<DevisProduct, String> parametersColumn;
    @FXML
    private TableColumn<DevisProduct, Double> priceColumn;
    @FXML
    private TableColumn<DevisProduct, Integer> quantityColumn;
    @FXML
    private TableColumn<DevisProduct, Double> totalColumn;
    @FXML
    private TableColumn<DevisProduct, Button> actionsColumn;
    @FXML
    private Label totalLabel;
    @FXML
    private Pane buttonbar;
    @FXML
    private ButtonBarController buttonbarController;
    @FXML
    private TextArea commentField;
    @FXML
    private TextField clientTextField;
    @FXML
    private TextField productTextField;

    private ListView<Customer> clientSuggestions;
    private ListView<Product> productSuggestions;
    private Stage clientPopup;
    private Stage productPopup;

    private Customer selectedClient;
    private Product selectedProduct;

    private List<Customer> allCustomers;
    private List<Product> allProducts;

    @Autowired
    private DevisService devisService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductService productService;
    @Autowired
    private DevisRepository devisRepository;
    
    private Devis currentDevis;
    private ObservableList<DevisProduct> devisProducts;

    @FXML
    private void initialize() {
        try {
            setupTable();
            setupAutoComplete();
            loadCustomersAndProducts();
            statusComboBox.getItems().addAll("En cours", "Validé", "Annulé");
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

    private void loadCustomersAndProducts() {
        allCustomers = new ArrayList<>();
        customerService.getAllCustomers().forEach(allCustomers::add);
        
        allProducts = new ArrayList<>();
        productService.getData().forEach(allProducts::add);
    }

    private void setupAutoComplete() {
        Platform.runLater(() -> {
            setupClientAutoComplete();
            setupProductAutoComplete();
        });
    }

    private void setupClientAutoComplete() {
        clientSuggestions = new ListView<>();
        clientPopup = new Stage(StageStyle.UNDECORATED);
        clientSuggestions.setPrefWidth(200);
        clientSuggestions.setPrefHeight(200);
        clientSuggestions.setStyle("-fx-border-color: #999999; -fx-border-width: 1; -fx-background-color: white;");
        Scene scene = new Scene(clientSuggestions);
        clientPopup.setScene(scene);
        
        clientTextField.textProperty().addListener((obs, oldText, newText) -> {
            if (clientTextField.getScene() == null) return;
            
            if (clientPopup.getOwner() == null) {
                clientPopup.initOwner(clientTextField.getScene().getWindow());
            }
            
            if (newText == null || newText.isEmpty()) {
                clientPopup.hide();
                return;
            }

            String search = newText.toLowerCase();
            List<Customer> matches = allCustomers.stream()
                    .filter(c -> (c.getName()).toLowerCase().contains(search))
                    .collect(Collectors.toList());
                    
            clientSuggestions.setItems(FXCollections.observableArrayList(matches));
            
            if (!matches.isEmpty()) {
                Bounds bounds = clientTextField.localToScreen(clientTextField.getBoundsInLocal());
                clientPopup.setX(bounds.getMinX());
                clientPopup.setY(bounds.getMaxY());
                if (!clientPopup.isShowing()) {
                    clientPopup.show();
                }
            } else {
                clientPopup.hide();
            }
        });

        // Add cell factory for client suggestions
        clientSuggestions.setCellFactory(lv -> new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        clientSuggestions.setOnMouseClicked(event -> {
            Customer selected = clientSuggestions.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectCustomer(selected);
            }
        });
    }

    private void setupProductAutoComplete() {
        productSuggestions = new ListView<>();
        productPopup = new Stage(StageStyle.UNDECORATED);
        productSuggestions.setPrefWidth(200);
        productSuggestions.setPrefHeight(200);
        productSuggestions.setStyle("-fx-border-color: #999999; -fx-border-width: 1; -fx-background-color: white;");
        Scene scene = new Scene(productSuggestions);
        productPopup.setScene(scene);
        
        productTextField.textProperty().addListener((obs, oldText, newText) -> {
            if (productTextField.getScene() == null) return;
            
            if (productPopup.getOwner() == null) {
                productPopup.initOwner(productTextField.getScene().getWindow());
            }
            
            if (newText == null || newText.isEmpty()) {
                productPopup.hide();
                return;
            }

            String search = newText.toLowerCase();
            List<Product> matches = allProducts.stream()
                    .filter(p -> p.getName().toLowerCase().contains(search))
                    .collect(Collectors.toList());
                    
            productSuggestions.setItems(FXCollections.observableArrayList(matches));
            
            if (!matches.isEmpty()) {
                Bounds bounds = productTextField.localToScreen(productTextField.getBoundsInLocal());
                productPopup.setX(bounds.getMinX());
                productPopup.setY(bounds.getMaxY());
                if (!productPopup.isShowing()) {
                    productPopup.show();
                }
            } else {
                productPopup.hide();
            }
        });

        // Add cell factory for product suggestions
        productSuggestions.setCellFactory(lv -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        productSuggestions.setOnMouseClicked(event -> {
            Product selected = productSuggestions.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectProduct(selected);
            }
        });
    }

    private void selectCustomer(Customer customer) {
        Platform.runLater(() -> {
            selectedClient = customer;
            String text =customer.getName();
            clientTextField.setText(text);
            clientTextField.positionCaret(text.length());
            clientTextField.requestFocus();
            clientPopup.hide();
        });
    }

    private void selectProduct(Product product) {
        Platform.runLater(() -> {
            selectedProduct = product;
            String text = product.getName();
            productTextField.setText(text);
            productTextField.positionCaret(text.length());
            productTextField.requestFocus();
            updateParameterFields(product);
            productPopup.hide();
        });
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
        totalLabel.setText(String.format("%.2f MAD", total));
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
        numeroDevisField.setText(generateInvoiceNumber());
        dateDevisField.setValue(LocalDateTime.now().toLocalDate());
        clientComboBox.setValue(null);
        statusComboBox.setValue("En cours");
        commentField.setText("");
        devisProducts.clear();
        updateTotal();
    }

    @Override
    public void render(SimpleEntity entity) {
        if (entity instanceof Devis) {
            currentDevis = (Devis) entity;
            numeroDevisField.setText(currentDevis.getNumeroDevis());
            dateDevisField.setValue(currentDevis.getDateDevis().toLocalDate());
            if (currentDevis.getClient() != null) {
                Customer client = currentDevis.getClient();
                clientTextField.setText(client.getName());
                selectedClient = client;
            }
            statusComboBox.setValue(currentDevis.getStatus());
            devisProducts.setAll(currentDevis.getProducts());
            commentField.setText(currentDevis.getComment());
            updateTotal();
        }
    }

    private boolean validateForm() {
        if (selectedClient == null) {
            showError("Le client est obligatoire");
            return false;
        }

        if (statusComboBox.getValue() == null) {
            showError("Le statut est obligatoire");
            return false;
        }

        if (dateDevisField.getValue() == null) {
            showError("La date est obligatoire");
            return false;
        }

        if (currentDevis.getProducts().isEmpty()) {
            showError("Au moins un produit est requis");
            return false;
        }

        return true;
    }

    @Override
    public boolean save() {
        if (!validateForm()) {
            return false;
        }

        if (currentDevis != null) {
            updateDevisFromInputs();
            devisService.save(currentDevis);
            // Update the displayed number after save
            numeroDevisField.setText(currentDevis.getNumeroDevis());
            // showSuccess("Devis enregistré avec succès");
            // close the form after saving
            return true;
        }
        return false;
    }

    private boolean validateInputs() {
        if (selectedClient == null) {
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
        currentDevis.setClient(selectedClient);
        currentDevis.setDateDevis(LocalDateTime.now());
        currentDevis.setStatus(statusComboBox.getValue());
        currentDevis.getProducts().clear();
        currentDevis.getProducts().addAll(devisProducts);
        currentDevis.setComment(commentField.getText());
    }

    private void clearProductInputs() {
        productTextField.clear();
        selectedProduct = null;
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
    private String generateInvoiceNumber() {
        String year = String.format("%02d", LocalDateTime.now().getYear() % 100);
        Devis lastDevis = devisRepository.findLastDevisForYear(year + "/");
        
        int nextNumber = 1;
        if (lastDevis != null) {
            String lastNumber = lastDevis.getNumeroDevis().split("/")[1];
            nextNumber = Integer.parseInt(lastNumber) + 1;
        }
        
        return String.format("%s/%02d", year, nextNumber);
    }
    /**
     * Handler for the "Vider" (Empty) button
     */
    @FXML
    public void handleEmpty() {
        // Clear the form and create a new Devis
        add();
        showSuccess("Le formulaire a été vidé");
    }

    /**
     * Handler for the "Enregistrer" (Save) button
     * This is separate from the CrudController's save() method
     * as it also shows a success message
     */
    @FXML
    public void handleSave(ActionEvent event) {
        save();
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    private void updateParameterFields(Product product) {
        parametersContainer.getChildren().clear();
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
            
            parametersContainer.getChildren().add(paramField);
        }
    }

}
