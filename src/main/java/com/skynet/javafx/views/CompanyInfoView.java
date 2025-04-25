package com.skynet.javafx.views;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.skynet.javafx.jfxsupport.AbstractFxmlView;
import com.skynet.javafx.jfxsupport.FXMLView;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.event.ActionEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.springframework.beans.factory.annotation.Autowired;
import com.skynet.javafx.service.CompanyInfoService;
import com.skynet.javafx.model.CompanyInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
@FXMLView("/fxml/companyInfo.fxml")
@Scope("prototype")
public class CompanyInfoView extends AbstractFxmlView {
    @FXML private TextField raisonSocialeField;
    @FXML private TextField adresseField;
    @FXML private Button logoButton;
    @FXML private ImageView logoPreview;
    @FXML private TextField phoneField;
    @FXML private TextField iceField;
    @FXML private TextField rcField;

    
    private File selectedLogoFile;
    
    @Autowired
    private CompanyInfoService service;
    
    // Regular expression for email validation
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    @FXML
    public void initialize() {
        // Initialize the view 
        logoButton.setOnAction(this::handleChooseLogo);
        
        // Add listener to phone field to restrict input to numbers only
        phoneField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Allow only digits and limit to 10 characters
                if (!newValue.matches("\\d*")) {
                    // If the new value contains non-digit characters, revert to old value
                    phoneField.setText(oldValue);
                } else if (newValue.length() > 10) {
                    // If the new value is longer than 10 digits, truncate it
                    phoneField.setText(newValue.substring(0, 10));
                }
            }
        });
    }
    
    @FXML
    private void handleChooseLogo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Logo Image");
        fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"),
            new ExtensionFilter("All Files", "*.*")
        );
        
        File file = fileChooser.showOpenDialog(logoButton.getScene().getWindow());
        if (file != null) {
            selectedLogoFile = file;
            try {
                Image image = new Image(new FileInputStream(file));
                logoPreview.setImage(image);
            } catch (IOException e) {
                e.printStackTrace();
                // Handle error loading image
            }
        }
    }
    
    /**
     * Validates all input fields
     * @return List of error messages, empty if all fields are valid
     */
    private List<String> validateInputs() {
        List<String> errors = new ArrayList<>();
        
        // Check required fields
        if (raisonSocialeField.getText().trim().isEmpty()) {
            errors.add("Raison sociale ne peut pas être vide");
        }
        
        if (adresseField.getText().trim().isEmpty()) {
            errors.add("Adresse ne peut pas être vide");
        }
        
        if (iceField.getText().trim().isEmpty()) {
            errors.add("ICE ne peut pas être vide");
        }
        
        if (rcField.getText().trim().isEmpty()) {
            errors.add("RC ne peut pas être vide");
        }
        
        // Validate phone number (must not be empty)
        String phone = phoneField.getText().trim();
        if (phone.isEmpty()) {
            errors.add("Numéro de téléphone ne peut pas être vide");
        }
        // No need to check for digits only since the listener prevents non-digit input
        
        return errors;
    }
    
    /**
     * Shows an error alert with the provided messages
     */
    private void showValidationError(List<String> errors) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur de validation");
        alert.setHeaderText("Veuillez corriger les erreurs suivantes:");
        
        StringBuilder content = new StringBuilder();
        for (String error : errors) {
            content.append("• ").append(error).append("\n");
        }
        
        alert.setContentText(content.toString());
        alert.showAndWait();
    }
    
    @FXML
    public void save() {
        // Validate inputs before saving
        List<String> validationErrors = validateInputs();
        if (!validationErrors.isEmpty()) {
            showValidationError(validationErrors);
            return;
        }
        
        CompanyInfo info = new CompanyInfo();
        info.setRaisonSociale(raisonSocialeField.getText().trim());
        info.setAdresse(adresseField.getText().trim());
        info.setTelephone(phoneField.getText().trim());
        info.setIce(iceField.getText().trim());
        info.setRc(rcField.getText().trim());
        
        // // Set email if provided
        // if (emailField.getText() != null && !emailField.getText().trim().isEmpty()) {
        //     info.setEmail(emailField.getText().trim());
        // }
        
        // Handle logo file if selected
        if (selectedLogoFile != null) {
            try {
                byte[] logoBytes = java.nio.file.Files.readAllBytes(selectedLogoFile.toPath());
                info.setLogo(logoBytes);
            } catch (IOException e) {
                e.printStackTrace();
                // Handle error reading file
            }
        }
        
        service.save(info);
        close();
    }
    
    @FXML
    public void cancel() {
        close();
    }
    
    private void close() {
        raisonSocialeField.getScene().getWindow().hide();
    }
}