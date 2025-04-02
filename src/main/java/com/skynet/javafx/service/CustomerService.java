package com.skynet.javafx.service;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.skynet.javafx.model.Customer;
import com.skynet.javafx.repository.CustomerRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Map;

@Service
public class CustomerService extends AbstractExcelExporter implements FrameService {

	private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);	
	
	@Autowired
	CustomerRepository customerRepository;

	@Override
	public List<Customer> getData() {
		Iterable<Customer> it = customerRepository.findAll();
		List<Customer> result = new ArrayList<>();
		it.forEach(result::add);
		return result;
	}

	@Override
	public void delete(Long id) {
		logger.debug("deleting customer with id: {}", id);		
		customerRepository.deleteById(id);		
	}

	public void save(Customer customer) {
		logger.debug("Saving customer: {}", customer);		
		customerRepository.save(customer);
	}
	// get all customers
	public List<Customer> getAllCustomers() {
		List<Customer> customers = new ArrayList<>();
		customerRepository.findAll().forEach(customers::add);
		return customers;
	}

	@Override
    public String getSheetName() {
        return "Customers";
    }

    @Override
    public List<String> getHeaders() {
        return Arrays.asList("ID", "Type", "Name", "Address", "Email", "ICE");
    }

    @Override
    public List<Map<String, Object>> getExportData() {
        List<Map<String, Object>> data = new ArrayList<>();
        List<Customer> customers = getAllCustomers();
        
        for (Customer customer : customers) {
            Map<String, Object> row = new HashMap<>();
            row.put("ID", customer.getId());
            row.put("Type", customer.getType());
            row.put("Name", customer.getName());
            row.put("Address", customer.getAddress());
            row.put("Email", customer.getEmail());
            row.put("ICE", customer.getICE());
            data.add(row);
        }
        
        return data;
    }

}
