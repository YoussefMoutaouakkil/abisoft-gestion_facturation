package com.skynet.javafx.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.skynet.javafx.model.Devis;
import com.skynet.javafx.model.Facture;
import com.skynet.javafx.model.SimpleEntity;
import com.skynet.javafx.repository.DevisRepository;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.skynet.javafx.service.AbstractExcelExporter;

@Service
public class DevisService extends AbstractExcelExporter implements FrameService {

    private static final Logger logger = LoggerFactory.getLogger(DevisService.class);

    @Autowired
    private DevisRepository devisRepository;

    @Override
    public List<Devis> getData() {
        List<Devis> devisList = new ArrayList<>();
        devisRepository.findAll().forEach(devisList::add);
        return devisList;
    }

    @Override
    public void delete(Long id) {
        // Check for null id and log an error if it's null
        if (id == null) {
            logger.error("Attempted to delete a Devis with null ID");
            return;
        }

        try {
            // Check if the entity exists before deleting
            if (devisRepository.existsById(id)) {
                devisRepository.deleteById(id);
                logger.info("Deleted Devis with ID: {}", id);
            } else {
                logger.warn("No Devis found with ID: {}", id);
            }
        } catch (Exception e) {
            logger.error("Error deleting Devis with ID: {}", id, e);
            throw e;
        }
    }

    // Additional methods specific to Devis
    public void save(Devis devis) {
        if (devis.getNumeroDevis() == null || devis.getNumeroDevis().isEmpty()) {
            devis.setNumeroDevis(generateDevisNumber());
        }
        devisRepository.save(devis);
    }

    private String generateDevisNumber() {
        String year = String.format("%02d", LocalDateTime.now().getYear() % 100);
        Devis lastDevis = devisRepository.findLastDevisForYear(year + "/");

        int nextNumber = 1;
        if (lastDevis != null) {
            String lastNumber = lastDevis.getNumeroDevis().split("/")[1];
            nextNumber = Integer.parseInt(lastNumber) + 1;
        }

        return String.format("%s/%02d", year, nextNumber);
    }

    /**
     * Returns all devis ordered by date (newest first)
     */
    public List<Devis> getAllDevis() {
        List<Devis> devisList = new ArrayList<>();
        devisRepository.findAll().forEach(devisList::add);
        // Sort by date (newest first)
        devisList.sort((d1, d2) -> d2.getDateDevis().compareTo(d1.getDateDevis()));
        return devisList;
    }

    @Override
    public String getSheetName() {
        return "Devis";
    }

    @Override
    public List<String> getHeaders() {
        return Arrays.asList("ID", "Numéro Devis", "Date", "Client", "Montant Total", "Status");
    }

    @Override
    public List<Map<String, Object>> getExportData() {
        List<Map<String, Object>> data = new ArrayList<>();
        List<Devis> factures = devisRepository.findAll();
        
        for (Devis facture : factures) {
            Map<String, Object> row = new HashMap<>();
            row.put("ID", facture.getId());
            row.put("Numéro Devis", facture.getNumeroDevis());
            row.put("Date", facture.getDateDevis());
            row.put("Client", facture.getClientName());
            row.put("Montant Total", facture.getMontantTotal());
            row.put("Status", facture.getStatus());
            data.add(row);
        }
        
        return data;
    }
}
