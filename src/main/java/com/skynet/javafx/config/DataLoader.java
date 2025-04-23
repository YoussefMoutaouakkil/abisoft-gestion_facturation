package com.skynet.javafx.config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.skynet.javafx.model.Category;
import com.skynet.javafx.model.CompanyInfo;
import com.skynet.javafx.model.Customer;
import com.skynet.javafx.model.MenuItem;
import com.skynet.javafx.model.Product;
import com.skynet.javafx.model.ProductParameter;
import com.skynet.javafx.repository.*;

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

    @Autowired
    private CompanyInfoRepository companyInfoRepository;

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("Starting data initialization...");
        
        if (menuItemRepository.count() == 0) {
            initializeMenuItems();
            logger.info("Menu items initialized successfully");
        }
        
        initializeCompanyInfo();
        initializeCustomers();
        initializeCategories();
        initializeProducts();
        initializeProductParameters();
        
        logger.info("Data initialization completed");
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
        createAndSaveMenuItem(5L, 1L, "master.invoices", "Factures", "factureService", "factureGridDef", "invoice_16x16.png");
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

    private void initializeCompanyInfo() {
        if (companyInfoRepository.count() == 0) {
            logger.info("Initializing company info...");
            
            CompanyInfo companyInfo = new CompanyInfo();
            companyInfo.setId(1L);
            companyInfo.setRaisonSociale("ABISOFT");
            companyInfo.setAdresse("123 Rue de l'Innovation, Casablanca");
            companyInfo.setTelephone("0522123456");
            // companyInfo.setEmail("contact@abisoft.ma");
            // companyInfo.setSiteWeb("www.abisoft.ma");
            companyInfo.setIce("001234567890123");
            companyInfo.setRc("RC12345");
            // companyInfo.setPatente("12345678");
            // companyInfo.setIf("12345678");
            // companyInfo.setCnss("1234567");
            
            companyInfoRepository.save(companyInfo);
            logger.info("Company info initialized successfully");
        }
    }

    private void initializeCustomers() {
        if (customerRepository.count() == 0) {
            logger.info("Initializing customers...");
            
            List<Customer> customers = new ArrayList<>();
            
            // Client 1
            Customer customer1 = new Customer();
            customer1.setId(1L);
            customer1.setType("Personne Physique");
            customer1.setName("Soufyane Majdoub");
            customer1.setAddress("Narjis");
            customer1.setEmail("soufyanemajdoub@gmail.com");
            customer1.setTel("0600112233");
            customers.add(customer1);
            
            // Client 2
            Customer customer2 = new Customer();
            customer2.setId(2L);
            customer2.setType("Entreprise");
            customer2.setName("Tech Solutions SARL");
            customer2.setAddress("Quartier industriel, Rabat");
            customer2.setEmail("contact@techsolutions.ma");
            customer2.setTel("0522998877");
            customer2.setICE("001122334455667");
            customers.add(customer2);
            
            // Client 3
            Customer customer3 = new Customer();
            customer3.setId(3L);
            customer3.setType("Personne Physique");
            customer3.setName("Ahmed Bennani");
            customer3.setAddress("Hay Riad, Rabat");
            customer3.setEmail("ahmed.bennani@gmail.com");
            customer3.setTel("0661223344");
            customers.add(customer3);
            
            customerRepository.saveAll(customers);
            logger.info("Customers initialized: {} customers created", customers.size());
        }
    }

    private void initializeCategories() {
        if (categoryRepository.count() == 0) {
            logger.info("Initializing product categories...");
            
            List<Category> categories = new ArrayList<>();
            
            // Catégorie 1
            Category category1 = new Category();
            category1.setId(1L);
            category1.setName("Services Informatiques");
            category1.setDescription("Services liés à l'informatique et au développement");
            categories.add(category1);
            
            // Catégorie 2
            Category category2 = new Category();
            category2.setId(2L);
            category2.setName("Matériel Informatique");
            category2.setDescription("Équipements et matériels informatiques");
            categories.add(category2);
            
            // Catégorie 3
            Category category3 = new Category();
            category3.setId(3L);
            category3.setName("Formations");
            category3.setDescription("Services de formation et coaching");
            categories.add(category3);
            
            categoryRepository.saveAll(categories);
            logger.info("Categories initialized: {} categories created", categories.size());
        }
    }

    private void initializeProducts() {
        if (productRepository.count() == 0) {
            logger.info("Initializing products...");
            
            List<Product> products = new ArrayList<>();
            
            Category servicesCategory = categoryRepository.findById(1L).orElse(null);
            Category hardwareCategory = categoryRepository.findById(2L).orElse(null);
            Category trainingCategory = categoryRepository.findById(3L).orElse(null);
            
            // Produit 1
            Product product1 = new Product();
            product1.setId(1L);
            product1.setName("Développement application web");
            product1.setDescription("Création d'un site web professionnel");
            product1.setPrice(10000.00);
            product1.setCategory(servicesCategory);
            products.add(product1);
            
            // Produit 2
            Product product2 = new Product();
            product2.setId(2L);
            product2.setName("PC Portable HP ProBook");
            product2.setDescription("Intel Core i7, 16GB RAM, 512 SSD");
            product2.setPrice(12000.00);
            product2.setCategory(hardwareCategory);
            products.add(product2);
            
            // Produit 3
            Product product3 = new Product();
            product3.setId(3L);
            product3.setName("Formation JavaFX");
            product3.setDescription("Formation complète sur JavaFX (20h)");
            product3.setPrice(5000.00);
            product3.setCategory(trainingCategory);
            products.add(product3);
            
            // Produit 4
            Product product4 = new Product();
            product4.setId(4L);
            product4.setName("Maintenance informatique");
            product4.setDescription("Service de maintenance mensuel");
            product4.setPrice(1500.00);
            product4.setCategory(servicesCategory);
            products.add(product4);
            
            productRepository.saveAll(products);
            logger.info("Products initialized: {} products created", products.size());
        }
    }
    
    private void initializeProductParameters() {
        if (productParameterRepository.count() == 0) {
            logger.info("Initializing product parameters...");
            
            // Get some products to associate parameters with
            Product webDev = productRepository.findById(1L).orElse(null);
            Product laptop = productRepository.findById(2L).orElse(null);
            
            List<ProductParameter> parameters = new ArrayList<>();
            
            // TVA Parameter
            ProductParameter tvaParam = new ProductParameter();
            tvaParam.setId(1L);
            tvaParam.setName("TVA");
            tvaParam.setProduct(webDev);
            tvaParam.addValue("20%", 1);
            parameters.add(tvaParam);
            
            // Size Parameter for Laptop
            ProductParameter sizeParam = new ProductParameter();
            sizeParam.setId(2L);
            sizeParam.setName("Screen Size");
            sizeParam.setProduct(laptop);
            sizeParam.addValue("13 inch", 5);
            sizeParam.addValue("15 inch", 8);
            sizeParam.addValue("17 inch", 3);
            parameters.add(sizeParam);
            
            // RAM Parameter for Laptop
            ProductParameter ramParam = new ProductParameter();
            ramParam.setId(3L);
            ramParam.setName("RAM");
            ramParam.setProduct(laptop);
            ramParam.addValue("8GB", 10);
            ramParam.addValue("16GB", 15);
            ramParam.addValue("32GB", 5);
            parameters.add(ramParam);
            
            productParameterRepository.saveAll(parameters);
            logger.info("Product parameters initialized: {} parameters created", parameters.size());
        }
    }
}
