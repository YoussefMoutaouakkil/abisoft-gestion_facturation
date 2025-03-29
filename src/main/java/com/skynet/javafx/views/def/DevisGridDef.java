package com.skynet.javafx.views.def;

import java.util.Arrays;
import java.util.List;
import com.skynet.javafx.model.Devis;
import com.skynet.javafx.views.DevisView;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

@Component("devisGridDef")
public class DevisGridDef implements FrameGridDef {

    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("Numéro", "Date", "Client", "Status", "Total");
    }

    @Override
    public List<String> getColumnDataName() {
        return Arrays.asList("numeroDevis", "dateDevis", "clientName", "status", "montantTotal");
    }

    @Override
    public List<Integer> getColumnSizes() {
        return Arrays.asList(100, 100, 150, 100, 100);
    }

    @Override
    public Class<?> getCreateView() {
        return DevisView.class;
    }

    @Override
    public String getTitlePopups() {
        return "Devis";
    }
}
