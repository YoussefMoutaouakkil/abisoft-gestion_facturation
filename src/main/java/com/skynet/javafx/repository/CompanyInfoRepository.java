package com.skynet.javafx.repository;

import com.skynet.javafx.model.CompanyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyInfoRepository extends JpaRepository<CompanyInfo, Long> {
    // Basic CRUD operations are inherited from JpaRepository
    // Add custom queries here if needed
}
