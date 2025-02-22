package com.skynet.javafx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.skynet.javafx.model.ProductParameter;
import com.skynet.javafx.repository.ProductParameterRepository;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductParameterService {
    
    @Autowired
    private ProductParameterRepository repository;
    
    @Transactional(readOnly = true)
    public List<ProductParameter> findByProductId(Long productId) {
        return repository.findByProductId(productId);
    }
    
    public void save(ProductParameter parameter) {
        repository.save(parameter);
    }
    
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
