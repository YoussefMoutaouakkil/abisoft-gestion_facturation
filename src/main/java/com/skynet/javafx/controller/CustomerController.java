package com.skynet.javafx.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.skynet.javafx.jfxsupport.FXMLController;
import com.skynet.javafx.model.Customer;
import com.skynet.javafx.model.SimpleEntity;
import com.skynet.javafx.service.CustomerService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

@FXMLController
public class CustomerController implements CrudController {

  private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

  @FXML
  private ButtonBarController buttonbarController;

  @FXML
  private TextField textName;
  @FXML
  private TextField textAddress;
  @FXML
  private TextField textEmail;
  @FXML
  private ComboBox<String> comboType;
  @FXML
  private TextField textICE;
  @FXML
  private Label labelICE;
  @FXML
  private TextArea commentField;
  @FXML
  private TextField telField;

  @Autowired
  private CustomerService customerService;

  private Customer customer;

  @FXML
  private void initialize() {
    logger.debug("initialize CustomerController");

    buttonbarController.setTarget(this);

    comboType.getItems().addAll("Personne Physique", "Entreprise");
    comboType.setValue("Personne Physique");

    comboType.setOnAction(e -> {
      boolean isEntreprise = "Entreprise".equals(comboType.getValue());
      textICE.setVisible(isEntreprise);
      labelICE.setVisible(isEntreprise);
    });
  }

  @Override
  public void add() {
    // TODO Auto-generated method stub
    // empty form
    customer = null;
    comboType.setValue("Personne Physique");
    textName.setText("");
    textAddress.setText("");
    textEmail.setText("");
    textICE.setText("");
    textICE.setVisible(false);
    labelICE.setVisible(false);
    commentField.setText("");
    telField.setText("");
  }

  @Override
  public void render(SimpleEntity entity) {
    customer = (Customer) entity;
    comboType.setValue(customer.getType());
    textName.setText(customer.getName());
    textAddress.setText(customer.getAddress());
    textEmail.setText(customer.getEmail());
    textICE.setText(customer.getICE());
    boolean isEntreprise = "Entreprise".equals(customer.getType());
    textICE.setVisible(isEntreprise);
    labelICE.setVisible(isEntreprise);
    commentField.setText(customer.getComment());
    telField.setText(customer.getTel());
  }

  private boolean isValidPhoneNumber(String phone) {
    return phone != null && phone.matches("\\d{10}");
  }

  private boolean isValidEmail(String email) {
    return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
  }

  private void showError(String message) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("Erreur de validation");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();  // Use show() instead of showAndWait()
  }

  private boolean validateForm() {
    String phoneNumber = telField.getText();
    if (!isValidPhoneNumber(phoneNumber)) {
      showError("Le numéro de téléphone doit contenir exactement 10 chiffres");
      return false;
    }

    String email = textEmail.getText();
    if (!isValidEmail(email)) {
      showError("Format d'email invalide");
      return false;
    }

    if (textName.getText().trim().isEmpty()) {
      showError("Le nom est obligatoire");
      return false;
    }

    return true;
  }

  @Override
  public void save() {
    if (!validateForm()) {
      return;
    }

    if (customer == null) {
      customer = new Customer();
    }

    customer.setType(comboType.getValue());
    customer.setName(textName.getText());
    customer.setAddress(textAddress.getText());
    customer.setEmail(textEmail.getText());
    customer.setICE(textICE.getText());
    customer.setComment(commentField.getText());
    customer.setTel(telField.getText());
    
    try {
      customerService.save(customer);
    } catch (Exception e) {
      showError("Erreur lors de l'enregistrement: " + e.getMessage());
    }
  }
}
