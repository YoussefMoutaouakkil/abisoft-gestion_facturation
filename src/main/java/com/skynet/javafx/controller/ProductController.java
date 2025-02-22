package com.skynet.javafx.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.skynet.javafx.jfxsupport.FXMLController;
import com.skynet.javafx.model.Category;
import com.skynet.javafx.model.Customer;
import com.skynet.javafx.model.Product;
import com.skynet.javafx.model.ProductParameter;
import com.skynet.javafx.model.SimpleEntity;
import com.skynet.javafx.model.ParameterValue;
import com.skynet.javafx.service.CustomerService;
import com.skynet.javafx.service.ProductService;
import com.skynet.javafx.service.ProductParameterService;
import com.skynet.javafx.service.CategoryService;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import javafx.collections.FXCollections;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
import javafx.fxml.Initializable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;  // Add this import
import javafx.scene.Node;

@Controller
@Scope("prototype")
@FXMLController
public class ProductController implements CrudController, Initializable {

  private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

  @FXML
  private TextField idColumn;
  @FXML
  private TextField nameColumn;
  @FXML
  private TextField priceColumn;
  @FXML
  private ComboBox<String> categoryComboBox;
  @FXML
  private TextField quantityColumn;

  // Remove table field since we don't use it anymore
  // @FXML private TableView<Product> table;
  
  @FXML
  private TableView<ProductParameter> parametersTable;
  
  @FXML
  private TableColumn<ProductParameter, String> paramNameColumn;
  
  @FXML
  private TableColumn<ProductParameter, Button> paramActionColumn;
  
  @FXML
  private TextField paramNameField;

  @FXML private TextField valueField;
  @FXML private TextField stockQuantityField;
  @FXML private ComboBox<ProductParameter> parameterComboBox;
  @FXML private TableView<ParameterValue> parameterValuesTable;
  @FXML private TableColumn<ParameterValue, String> parameterNameColumn;
  @FXML private TableColumn<ParameterValue, String> valueColumn;
  @FXML private TableColumn<ParameterValue, Integer> stockColumn;
  @FXML private TableColumn<ParameterValue, Button> valueActionsColumn;

  @Autowired
  private ProductService productService;

  @Autowired
  private CategoryService categoryService;

  @Autowired
  private ProductParameterService parameterService;

  private Product product;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    logger.debug("initialize ProductController");

    // Remove table initialization code since we don't need it anymore

    // Initialize parameter table columns
    paramNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    
    // Add action column for parameters
    paramActionColumn.setCellFactory(col -> new TableCell<ProductParameter, Button>() {
        private final Button deleteButton = new Button("Delete");
        {
            deleteButton.setOnAction(e -> {
                ProductParameter param = getTableView().getItems().get(getIndex());
                parametersTable.getItems().remove(param);
                if (param.getProduct() != null) {
                    param.getProduct().removeParameter(param);
                }
            });
        }
        @Override
        protected void updateItem(Button item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : deleteButton);
        }
    });
    
    // Make parameter table editable
    parametersTable.setEditable(true);
    paramNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    
    // Setup parameter values table
    setupParameterValuesTable();
    
    // Update parameter combo box when parameter table selection changes
    parametersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
        parameterComboBox.setValue(newVal);
        updateParameterValuesTable();
    });

    // Populate the categoryComboBox with the data from the categoryService show names
    categoryComboBox.getItems().addAll(categoryService.getData().stream().map(Category::getName).collect(Collectors.toList()));
    
    // Add input validation
    setupInputValidation();

    // Add converter for parameter combo box
    parameterComboBox.setConverter(new javafx.util.StringConverter<ProductParameter>() {
        @Override
        public String toString(ProductParameter param) {
            return param != null ? param.getName() : "";
        }
        
        @Override
        public ProductParameter fromString(String string) {
            return null;
        }
    });
  }

  private void setupInputValidation() {
    // Number-only filter for quantity
    UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.matches("([0-9]*)?")) {
            return change;
        }
        return null;
    };
    
    // Decimal filter for price
    UnaryOperator<TextFormatter.Change> decimalFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.matches("\\d*\\.?\\d*")) {
            return change;
        }
        return null;
    };

    quantityColumn.setTextFormatter(new TextFormatter<>(integerFilter));
    priceColumn.setTextFormatter(new TextFormatter<>(decimalFilter));
    
    // Add stock quantity field validation
    stockQuantityField.textProperty().addListener((observable, oldValue, newValue) -> {
        if (!newValue.matches("\\d*")) {
            stockQuantityField.setText(newValue.replaceAll("[^\\d]", ""));
        }
    });
  }

  @Override
  public void add() {
    // TODO Auto-generated method stub
  }

  @Override
  public void render(SimpleEntity entity) {
    product = (Product) entity;
    nameColumn.setText(product.getName());
    priceColumn.setText(product.getPrice().toString());
    categoryComboBox.setValue(product.getCategory() != null ? product.getCategory().getName() : null);
    quantityColumn.setText(product.getQuantity().toString());
    quantityColumn.setDisable(true); // Make quantity read-only
    
    // Load parameters and set up combo box
    if (product != null && product.getId() != null) {
        parametersTable.setItems(FXCollections.observableArrayList(product.getParameters()));
        parameterComboBox.setItems(FXCollections.observableArrayList(product.getParameters()));
    }
  }

  @Override
  public void save() {
    try {
      if (product == null) {
        product = new Product();
      }
      product.setName(nameColumn.getText());
      product.setPrice(Double.parseDouble(priceColumn.getText().isEmpty() ? "0" : priceColumn.getText()));
      product.setCategory(categoryService.findByName(categoryComboBox.getValue()));
      productService.save(product);

      // Save parameters
      for (ProductParameter param : parametersTable.getItems()) {
        if (param.getProduct() == null) {
          param.setProduct(product);
        }
        parameterService.save(param);
      }
      
      // Close the window after saving
      Stage stage = (Stage) nameColumn.getScene().getWindow();
      stage.close();
      
    } catch (NumberFormatException e) {
      logger.error("Error parsing numeric values", e);
    }
  }

  // Add method to update quantity display when parameters change
  private void updateQuantityDisplay() {
    if (product != null) {
        int totalQuantity = product.getParameters().stream()
                .mapToInt(ProductParameter::getStockQuantity)
                .sum();
        quantityColumn.setText(String.valueOf(totalQuantity));
    }
  }

  @FXML
  private void addParameter() {
    if (paramNameField.getText().isEmpty()) {
        showError("Parameter name is required");
        return;
    }
    
    ProductParameter param = new ProductParameter();
    param.setName(paramNameField.getText());
    param.setProduct(product);
    
    parametersTable.getItems().add(param);
    parameterComboBox.getItems().add(param);
    
    paramNameField.clear();
  }

  @FXML
  public void handleNameEdit(TableColumn.CellEditEvent<ProductParameter, String> event) {
      ProductParameter param = event.getRowValue();
      param.setName(event.getNewValue());
      if (param.getProduct() != null) {
          parameterService.save(param);
      }
  }

  // Remove or comment out handleQuantityEdit method since we don't use it anymore
  /*
  @FXML
  public void handleQuantityEdit(TableColumn.CellEditEvent<ProductParameter, Integer> event) {
      // This method is no longer used
  }
  */

  private void setupParameterValuesTable() {
    parameterNameColumn.setCellValueFactory(data -> 
        new SimpleStringProperty(data.getValue().getParameter().getName()));
    valueColumn.setCellValueFactory(data -> 
        new SimpleStringProperty(data.getValue().getValue()));
    stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
    
    valueActionsColumn.setCellFactory(col -> new TableCell<ParameterValue, Button>() {
        private final Button deleteButton = new Button("Delete");
        {
            deleteButton.setOnAction(e -> {
                ParameterValue value = getTableView().getItems().get(getIndex());
                value.getParameter().getValues().remove(value);
                updateParameterValuesTable();
                updateQuantityDisplay(); // Add this line to update the total quantity
            });
        }
        @Override
        protected void updateItem(Button item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : deleteButton);
        }
    });
  }

  @FXML
  private void addParameterValue() {
    ProductParameter parameter = parameterComboBox.getValue();
    String value = valueField.getText();
    String stockQuantityText = stockQuantityField.getText();
    
    if (parameter == null || value.isEmpty() || stockQuantityText.isEmpty()) {
        showError("Please fill all fields");
        return;
    }
    
    try {
        int stockQuantity = Integer.parseInt(stockQuantityText);
        if (stockQuantity < 0) {
            showError("Stock quantity must be positive");
            return;
        }
        
        parameter.addValue(value, stockQuantity);
        updateParameterValuesTable();
        updateQuantityDisplay(); // Add this line to update the total quantity
        clearParameterValueFields();
        
    } catch (NumberFormatException e) {
        showError("Invalid stock quantity");
    }
  }

  private void updateParameterValuesTable() {
    ProductParameter selectedParam = parameterComboBox.getValue();
    if (selectedParam != null) {
        parameterValuesTable.setItems(FXCollections.observableArrayList(selectedParam.getValues()));
    } else {
        parameterValuesTable.getItems().clear();
    }
  }

  private void clearParameterValueFields() {
    valueField.clear();
    stockQuantityField.clear();
  }

  // Add error dialog helper method
  private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  @FXML
  private void cancel(ActionEvent event) {
      Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      stage.close();
  }
}
