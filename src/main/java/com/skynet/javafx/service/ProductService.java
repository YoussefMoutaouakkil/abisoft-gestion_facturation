package com.skynet.javafx.service;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.skynet.javafx.model.Customer;
import com.skynet.javafx.model.Product;
import com.skynet.javafx.repository.CustomerRepository;
import com.skynet.javafx.repository.ProductRepository;

@Service
public class ProductService implements FrameService {

	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);	
	
	@Autowired
	ProductRepository productRepository;

	@Override
	public List<Product> getData() {
		List<Product> result = productRepository.findAll();
		return result;
	}

	@Override
	public void delete(Long id) {
		logger.debug("deleting product with id: {}", id);		
		productRepository.deleteById(id);		
	}

	public void save(Product product) {
		logger.debug("Saving customer: {}", product);		
		productRepository.save(product);
	}

}
