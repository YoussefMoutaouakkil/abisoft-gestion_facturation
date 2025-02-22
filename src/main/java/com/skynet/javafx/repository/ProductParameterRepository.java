package com.skynet.javafx.repository;

import com.skynet.javafx.model.ProductParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductParameterRepository extends JpaRepository<ProductParameter, Long> {
    List<ProductParameter> findByProductId(Long productId);
}
