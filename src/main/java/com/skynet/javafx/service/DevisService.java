package com.skynet.javafx.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.skynet.javafx.model.Devis;
import com.skynet.javafx.model.SimpleEntity;
import com.skynet.javafx.repository.DevisRepository;
import java.time.LocalDateTime;

@Service
public class DevisService implements FrameService {
    
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
        devisRepository.deleteById(id);
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
}
