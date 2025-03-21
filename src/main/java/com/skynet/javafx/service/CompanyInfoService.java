package com.skynet.javafx.service;

import com.skynet.javafx.model.CompanyInfo;
import com.skynet.javafx.model.SimpleEntity;
import com.skynet.javafx.repository.CompanyInfoRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyInfoService implements FrameService {
    @Autowired
    private CompanyInfoRepository repository;

    public CompanyInfo save(CompanyInfo companyInfo) {
        // Get existing company info if any
        List<CompanyInfo> existingInfo = repository.findAll();
        
        if (!existingInfo.isEmpty()) {
            // If company info exists, update it instead of creating a new one
            CompanyInfo existing = existingInfo.get(0);
            existing.setRaisonSociale(companyInfo.getRaisonSociale());
            existing.setAdresse(companyInfo.getAdresse());
            
            // Only update logo if a new one was provided
            if (companyInfo.getLogo() != null && companyInfo.getLogo().length > 0) {
                existing.setLogo(companyInfo.getLogo());
            }
            
            return repository.save(existing);
        } else {
            // If no company info exists yet, create a new one
            return repository.save(companyInfo);
        }
    }

    public List<CompanyInfo> getCompanyInfo() {
        List<CompanyInfo> companyInfo = repository.findAll();
        return companyInfo;
    }

    @Override
    public List<? extends SimpleEntity> getData() {
        return getCompanyInfo();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
