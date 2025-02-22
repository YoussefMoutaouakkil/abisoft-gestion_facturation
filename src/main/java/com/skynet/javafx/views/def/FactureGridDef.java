package com.skynet.javafx.views.def;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;
import com.skynet.javafx.views.FactureView;

@Component("factureGridDef")
public class FactureGridDef implements FrameGridDef {
    
    public static String COLUMN_NAMES[] = 
        { "N° Facture", "Date", "Montant", "Client", "Status" };
    public static String COLUMN_DATA_NAMES[] = 
        { "numeroFacture", "dateFacture", "montantTotal", "clientName", "status" };  // Changed "client" to "clientName"
    public static Integer COLUMN_SIZES[] = { 120, 150, 100, 150, 100 };
    public static String TITLE_POPUPS = "Gestion des Factures";

    @Override
    public List<String> getColumnNames() {
        return Arrays.asList(COLUMN_NAMES);
    }

    @Override
    public List<String> getColumnDataName() {
        return Arrays.asList(COLUMN_DATA_NAMES);
    }

    @Override
    public List<Integer> getColumnSizes() {
        return Arrays.asList(COLUMN_SIZES);
    }

    @Override
    public Class<?> getCreateView() {
        return FactureView.class;
    }

    @Override
    public String getTitlePopups() {
        return TITLE_POPUPS;
    }
}
