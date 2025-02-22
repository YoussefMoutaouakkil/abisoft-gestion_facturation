package com.skynet.javafx.service;

import com.skynet.javafx.model.Facture;
import com.skynet.javafx.model.SimpleEntity;
import com.skynet.javafx.repository.FactureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FactureService implements FrameService {

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
}
