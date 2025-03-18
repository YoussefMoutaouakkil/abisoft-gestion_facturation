package com.skynet.javafx.service;

import com.skynet.javafx.model.Facture;
import com.skynet.javafx.model.SimpleEntity;
import com.skynet.javafx.repository.FactureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FactureService extends AbstractExcelExporter implements FrameService {

    @Autowired
    private FactureRepository factureRepository;

    @Override
    public List<? extends SimpleEntity> getData() {
        return factureRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        factureRepository.deleteById(id);
    }

    // Additional service methods
    public Facture save(Facture facture) {
        return factureRepository.save(facture);
    }

    public Facture findById(Long id) {
        return factureRepository.findById(id).orElse(null);
    }

    @Override
    public String getSheetName() {
        return "Factures";
    }

    @Override
    public List<String> getHeaders() {
        return Arrays.asList("ID", "Numéro Facture", "Date", "Client", "Montant Total", "Status");
    }

    @Override
    public List<Map<String, Object>> getExportData() {
        List<Map<String, Object>> data = new ArrayList<>();
        List<Facture> factures = factureRepository.findAll();
        
        for (Facture facture : factures) {
            Map<String, Object> row = new HashMap<>();
            row.put("ID", facture.getId());
            row.put("Numéro Facture", facture.getNumeroFacture());
            row.put("Date", facture.getDateFacture());
            row.put("Client", facture.getClientName());
            row.put("Montant Total", facture.getMontantTotal());
            row.put("Status", facture.getStatus());
            data.add(row);
        }
        
        return data;
    }
}
