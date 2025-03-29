package com.skynet.javafx.views;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.skynet.javafx.jfxsupport.AbstractFxmlView;
import com.skynet.javafx.jfxsupport.FXMLView;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.event.ActionEvent;

import org.springframework.beans.factory.annotation.Autowired;
import com.skynet.javafx.service.CompanyInfoService;
import com.skynet.javafx.model.CompanyInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
@FXMLView("/fxml/companyInfo.fxml")
@Scope("prototype")
public class CompanyInfoView extends AbstractFxmlView {
    @FXML private TextField raisonSocialeField;
    @FXML private TextField adresseField;
    @FXML private Button logoButton;
    @FXML private ImageView logoPreview;
    @FXML private TextField phoneField;
    
    private File selectedLogoFile;
    
    @Autowired
    private CompanyInfoService service;
    
    @FXML
    public void initialize() {
        // Initialize the view 
        logoButton.setOnAction(this::handleChooseLogo);
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
    
    @FXML
    public void save() {
        CompanyInfo info = new CompanyInfo();
        info.setRaisonSociale(raisonSocialeField.getText());
        info.setAdresse(adresseField.getText());
        info.setTelephone(phoneField.getText());
        
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