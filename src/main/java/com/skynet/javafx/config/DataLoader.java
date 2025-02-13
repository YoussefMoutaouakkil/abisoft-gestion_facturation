package com.skynet.javafx.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.skynet.javafx.model.Customer;
import com.skynet.javafx.model.MenuItem;
import com.skynet.javafx.repository.CustomerRepository;
import com.skynet.javafx.repository.MenuItemRepository;

@Component("dataLoader")
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (menuItemRepository.count() == 0) {
            logger.info("Initializing menu items...");
            initializeMenuItems();
        }
        initializeCustomers();
    }

    private void initializeMenuItems() {
        MenuItem root = new MenuItem();
        root.setId(1L);
        root.setParent(-1L);
        root.setKey("master");
        root.setValue("System");
        root.setExpanded(true);
        menuItemRepository.save(root);

        createAndSaveMenuItem(2L, 1L, "master.customers", "Mes clients", "customerService", "customerGridDef", "customers_16x16.png");
        createAndSaveMenuItem(3L, 1L, "master.categories", "Categories des produits", "categoryService", "categoryGridDef", "category_16x16.png");
        createAndSaveMenuItem(4L, 1L, "master.products", "Mes produits", "productService", "productGridDef", "product_16x16.png");
        createAndSaveMenuItem(5L, 1L, "master.invoices", "Factures", null, null, "invoice_16x16.png");
        createAndSaveMenuItem(6L, 1L, "master.devis", "Devis", null, null, "invoice_16x16.png");
    }

    private void createAndSaveMenuItem(Long id, Long parent, String key, String value, String service, String gridDef, String image) {
        MenuItem item = new MenuItem();
        item.setId(id);
        item.setParent(parent);
        item.setKey(key);
        item.setValue(value);
        item.setService(service);
        item.setGridDef(gridDef);
        item.setExpanded(false);
        item.setImage(image);
        menuItemRepository.save(item);
    }

    private void initializeCustomers() {
        if (customerRepository.count() == 0) {
            logger.info("Initializing customers...");
            
            Customer customer = new Customer();
            customer.setId(1L);
            customer.setFirstname("soufyane");
            customer.setLastname("majdoub");
            customer.setAddress("Narjis");
            customer.setEmail("soufyanemajdoub@gmail.com");
            customerRepository.save(customer);
        }
    }
}
