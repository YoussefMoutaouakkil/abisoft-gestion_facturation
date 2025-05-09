package com.skynet.javafx.controller;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.skynet.javafx.jfxsupport.FXMLController;
import com.skynet.javafx.model.Category;
import com.skynet.javafx.model.Customer;
import com.skynet.javafx.model.Product;
import com.skynet.javafx.model.SimpleEntity;
import com.skynet.javafx.service.CategoryService;
import com.skynet.javafx.service.CustomerService;
import com.skynet.javafx.service.ProductService;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

@FXMLController
public class CategoryController implements CrudController {

  private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

  @FXML
  private ButtonBarController buttonbarController;

  @FXML
  private TextField idColumn;
  @FXML
  private TextField nameColumn;
  @FXML
  private TextArea descriptionColumn;
  @FXML
  private TextArea commentColumn;


  @Autowired
  private CategoryService categoryService;

  private Category category;

  @FXML
  private void initialize() {
    logger.debug("initialize CategoryController");

    buttonbarController.setTarget(this);
  }

  @Override
  public void add() {
    // TODO Auto-generated method stub
  }

  @Override
  public void render(SimpleEntity entity) {
    category = (Category) entity;
    nameColumn.setText(category.getName());
    descriptionColumn.setText(category.getDescription());
    commentColumn.setText(category.getComment());
  }

  private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Erreur de validation");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  private boolean validateForm() {
    if (nameColumn.getText().trim().isEmpty()) {
      showError("Le nom de la catégorie est obligatoire");
      return false;
    }
    return true;
  }

  @Override
  public boolean save() {
    if (!validateForm()) {
      return false;
    }

    if (category == null) {
      category = new Category();
    }
    category.setName(nameColumn.getText());
    category.setDescription(descriptionColumn.getText());
    category.setComment(commentColumn.getText());
    categoryService.save(category);
    return true;
  }
}
