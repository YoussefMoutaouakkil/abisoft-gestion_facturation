package com.skynet.javafx.service;

import com.skynet.javafx.model.Category;
import com.skynet.javafx.repository.CategoryRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Service
@Transactional
public class CategoryService extends AbstractExcelExporter implements FrameService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);    
    
    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public List<Category> getData() {
        List<Category> result = categoryRepository.findAll();
        return result;
    }

    @Override
    public void delete(Long id) {
        logger.debug("deleting customer with id: {}", id);        
        categoryRepository.deleteById(id);        
    }

    public void save(Category category) {
        logger.debug("Saving category: {}", category);        
        categoryRepository.save(category);
    }

    // findByName
    public Category findByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public String getSheetName() {
        return "Categories";
    }

    @Override
    public List<String> getHeaders() {
        return Arrays.asList("ID", "Name", "Description");
    }

    @Override
    public List<Map<String, Object>> getExportData() {
        List<Map<String, Object>> data = new ArrayList<>();
        List<Category> categories = getData();
        
        for (Category category : categories) {
            Map<String, Object> row = new HashMap<>();
            row.put("ID", category.getId());
            row.put("Name", category.getName());
            row.put("Description", category.getDescription());
            data.add(row);
        }
        
        return data;
    }
}
