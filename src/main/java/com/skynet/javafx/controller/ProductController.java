package com.skynet.javafx.controller;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.skynet.javafx.jfxsupport.FXMLController;
import com.skynet.javafx.model.Category;
import com.skynet.javafx.model.Customer;
import com.skynet.javafx.model.Product;
import com.skynet.javafx.model.SimpleEntity;
import com.skynet.javafx.service.CustomerService;
import com.skynet.javafx.service.ProductService;
import com.skynet.javafx.service.CategoryService;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextFormatter;

@FXMLController
public class ProductController implements CrudController {

  private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

  @FXML
  private ButtonBarController buttonbarController;

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

  @Autowired
  private ProductService productService;

  @Autowired
  private CategoryService categoryService;

  private Product product;

  @FXML
  private void initialize() {
    logger.debug("initialize ProductController");

    buttonbarController.setTarget(this);
    
    // Populate the categoryComboBox with the data from the categoryService show names
    categoryComboBox.getItems().addAll(categoryService.getData().stream().map(Category::getName).collect(Collectors.toList()));
    
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
    categoryComboBox.setValue(product.getCategory().getName());
    quantityColumn.setText(product.getQuantity().toString());
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
      product.setQuantity(Integer.parseInt(quantityColumn.getText().isEmpty() ? "0" : quantityColumn.getText()));
      productService.save(product);
    } catch (NumberFormatException e) {
      logger.error("Error parsing numeric values", e);
      // You might want to show an error dialog here
    }
  }

  
}
