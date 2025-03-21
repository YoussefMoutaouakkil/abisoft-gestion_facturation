package com.skynet.javafx.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.skynet.javafx.model.Category;
import com.skynet.javafx.model.Customer;
import com.skynet.javafx.model.MenuItem;
import com.skynet.javafx.model.Product;
import com.skynet.javafx.model.ProductParameter;
import com.skynet.javafx.repository.CustomerRepository;
import com.skynet.javafx.repository.MenuItemRepository;
import com.skynet.javafx.repository.ProductRepository;
import com.skynet.javafx.repository.CategoryRepository;
import com.skynet.javafx.repository.ProductParameterRepository;

@Component("dataLoader")
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductParameterRepository productParameterRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (menuItemRepository.count() == 0) {
            logger.info("Initializing menu items...");
            initializeMenuItems();
        }
        initializeCustomers();
        initializeCategories();
        initializeProducts();
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
        createAndSaveMenuItem(5L, 1L, "master.invoices", "Factures", "factureService", "factureGridDef", "invoice_16x16.png"); // Fix gridDef name
        createAndSaveMenuItem(6L, 1L, "master.devis", "Devis", "devisService", "devisGridDef", "invoice_16x16.png");
        createAndSaveMenuItem(7L, 1L, "master.info", "Mes informations", "companyInfoService", "companyInfoGridDef", "customers_16x16.png");
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

    private void initializeCategories() {
        if (categoryRepository.count() == 0) {
            logger.info("Initializing categories...");
            
            Category electronics = new Category();
            electronics.setName("Electronics");
            categoryRepository.save(electronics);
            
            Category clothing = new Category();
            clothing.setName("Clothing");
            categoryRepository.save(clothing);
            
            Category food = new Category();
            food.setName("Food");
            categoryRepository.save(food);
        }
    }

    private void initializeProducts() {
        if (productRepository.count() == 0) {
            logger.info("Initializing products...");
            
            Category clothing = categoryRepository.findByName("Clothing");
            
            // Create T-shirt product
            Product tshirt = new Product();
            tshirt.setName("Cotton T-Shirt");
            tshirt.setPrice(29.99);
            tshirt.setCategory(clothing);
            
            // Create Size parameter
            ProductParameter sizeParam = new ProductParameter();
            sizeParam.setName("Size");
            sizeParam.setProduct(tshirt);
            
            // Add size values with stock
            sizeParam.addValue("S", 10);
            sizeParam.addValue("M", 15);
            sizeParam.addValue("L", 20);
            sizeParam.addValue("XL", 8);
            
            // Create Color parameter
            ProductParameter colorParam = new ProductParameter();
            colorParam.setName("Color");
            colorParam.setProduct(tshirt);
            
            // Add color values with stock
            colorParam.addValue("Red", 12);
            colorParam.addValue("Blue", 15);
            colorParam.addValue("Black", 20);
            colorParam.addValue("White", 18);
            
            // Add parameters to product
            tshirt.addParameter(sizeParam);
            tshirt.addParameter(colorParam);
            
            // Save product
            productRepository.save(tshirt);
            
            // Create Pants product
            Product pants = new Product();
            pants.setName("Jeans");
            pants.setPrice(49.99);
            pants.setCategory(clothing);
            
            // Create Size parameter for pants
            ProductParameter pantsSizeParam = new ProductParameter();
            pantsSizeParam.setName("Size");
            pantsSizeParam.setProduct(pants);
            
            // Add size values with stock
            pantsSizeParam.addValue("30", 10);
            pantsSizeParam.addValue("32", 15);
            pantsSizeParam.addValue("34", 12);
            pantsSizeParam.addValue("36", 8);
            
            // Create Color parameter for pants
            ProductParameter pantsColorParam = new ProductParameter();
            pantsColorParam.setName("Color");
            pantsColorParam.setProduct(pants);
            
            // Add color values with stock
            pantsColorParam.addValue("Blue", 20);
            pantsColorParam.addValue("Black", 25);
            pantsColorParam.addValue("Gray", 15);
            
            // Add parameters to pants
            pants.addParameter(pantsSizeParam);
            pants.addParameter(pantsColorParam);
            
            // Save pants
            productRepository.save(pants);
        }
    }
}
