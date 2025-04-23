package com.skynet.javafx.views.def;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import com.skynet.javafx.model.CompanyInfo;
import com.skynet.javafx.views.CompanyInfoView;
@Component("companyInfoGridDef")
public class CompanyInfoGridDef implements FrameGridDef {
    // private TextField raisonSocialeField;
    // private TextField adresseField;
    // private Button logoButton;
    // private ImageView logoPreview;

    // protected void initFields() {
    //     raisonSocialeField = addTextField("Raison Sociale", "raisonSociale");
    //     adresseField = addTextField("Adresse", "adresse");
    //     logoButton = addImageUploadButton("Logo", "logo", logoPreview = new ImageView());
    // }

    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("Raison Sociale", "Adresse","Telephone","ICE","RC");
    }

    @Override
    public List<String> getColumnDataName() {
        return Arrays.asList("raisonSociale", "adresse","telephone","ice","rc");
    }

    @Override
    public List<Integer> getColumnSizes() {
        return Arrays.asList(200, 300, 200, 200, 200);
    }

    @Override
    public Class<?> getCreateView() {
        return CompanyInfoView.class;
    }

    @Override
    public String getTitlePopups() {
        return "Information de l'entreprise";
    }
}
