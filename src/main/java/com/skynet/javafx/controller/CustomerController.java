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
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;

@FXMLController
public class CustomerController implements CrudController {

  private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

  @FXML
  private ButtonBarController buttonbarController;

  @FXML
  private TextField textFirstname;
  @FXML
  private TextField textLastname;
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
    textFirstname.setText("");
    textLastname.setText("");
    textAddress.setText("");
    textEmail.setText("");
    textICE.setText("");
    textICE.setVisible(false);
    labelICE.setVisible(false);
  }

  @Override
  public void render(SimpleEntity entity) {
    customer = (Customer) entity;
    comboType.setValue(customer.getType());
    textFirstname.setText(customer.getFirstname());
    textLastname.setText(customer.getLastname());
    textAddress.setText(customer.getAddress());
    textEmail.setText(customer.getEmail());
    textICE.setText(customer.getICE());
    boolean isEntreprise = "Entreprise".equals(customer.getType());
    textICE.setVisible(isEntreprise);
    labelICE.setVisible(isEntreprise);
  }

  @Override
  public void save() {
    if (customer == null) {
      customer = new Customer();
    }
    customer.setType(comboType.getValue());
    customer.setFirstname(textFirstname.getText());
    customer.setLastname(textLastname.getText());
    customer.setAddress(textAddress.getText());
    customer.setEmail(textEmail.getText());
    customer.setICE(textICE.getText());
    customerService.save(customer);
  }
}
