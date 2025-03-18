package com.skynet.javafx.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.skynet.javafx.model.Customer;
import com.skynet.javafx.model.Product;
import com.skynet.javafx.repository.CustomerRepository;
import com.skynet.javafx.repository.ProductRepository;

@Service
public class ProductService extends AbstractExcelExporter implements FrameService {

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

	@Override
	public String getSheetName() {
		return "Products";
	}

	@Override
	public List<String> getHeaders() {
		return Arrays.asList("ID", "Name", "Description", "Price", "Category");
	}

	@Override
	public List<Map<String, Object>> getExportData() {
		List<Map<String, Object>> data = new ArrayList<>();
		List<Product> products = getData();
		
		for (Product product : products) {
			Map<String, Object> row = new HashMap<>();
			row.put("ID", product.getId());
			row.put("Name", product.getName());
			row.put("Description", product.getDescription());
			row.put("Price", product.getPrice());
			row.put("Category", product.getCategory() != null ? product.getCategory().getName() : "");
			data.add(row);
		}
		
		return data;
	}
}
