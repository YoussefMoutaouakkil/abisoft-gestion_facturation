package com.skynet.javafx.repository;

import com.skynet.javafx.model.FactureProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactureProductRepository extends JpaRepository<FactureProduct, Long> {
    // Add custom query methods if needed
}

