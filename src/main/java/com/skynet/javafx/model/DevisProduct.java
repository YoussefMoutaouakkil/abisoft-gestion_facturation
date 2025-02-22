package com.skynet.javafx.model;

import javafx.beans.property.*;
import javax.persistence.*;

@Entity
@Table(name = "devis_products")
public class DevisProduct extends SimpleEntity {
    
    @ManyToOne
    @JoinColumn(name = "devis_id")
    private Devis devis;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(name = "parameter_description")
    private String parameterDescription;
    
    // Transient properties for JavaFX binding
    private transient StringProperty productNameProperty;
    private transient StringProperty parametersProperty;
    private transient DoubleProperty priceProperty;
    private transient IntegerProperty quantityProperty;
    private transient DoubleProperty totalProperty;
    
    public StringProperty productNameProperty() {
        if (productNameProperty == null) {
            productNameProperty = new SimpleStringProperty("");
            if (product != null) {
                productNameProperty.bind(product.nameProperty());
            }
        }
        return productNameProperty;
    }
    
    public StringProperty parametersProperty() {
        if (parametersProperty == null) {
            parametersProperty = new SimpleStringProperty(parameterDescription != null ? parameterDescription : "");
        }
        return parametersProperty;
    }
    
    public DoubleProperty priceProperty() {
        if (priceProperty == null) {
            priceProperty = new SimpleDoubleProperty(price != null ? price : 0.0);
        }
        return priceProperty;
    }
    
    public IntegerProperty quantityProperty() {
        if (quantityProperty == null) {
            quantityProperty = new SimpleIntegerProperty(quantity != null ? quantity : 0);
        }
        return quantityProperty;
    }
    
    public DoubleProperty totalProperty() {
        if (totalProperty == null) {
            totalProperty = new SimpleDoubleProperty(this, "total");
            totalProperty.bind(priceProperty().multiply(quantityProperty()));
        }
        return totalProperty;
    }
    
    public DevisProduct() {}
    
    // Getters and Setters
    public Devis getDevis() { 
        return devis; 
    }
    
    public void setDevis(Devis devis) { 
        this.devis = devis; 
    }
    
    public Product getProduct() { 
        return product; 
    }
    
    public void setProduct(Product product) { 
        this.product = product;
        if (productNameProperty != null && product != null) {
            productNameProperty.bind(product.nameProperty());
        }
    }
    
    public Integer getQuantity() { 
        return quantity; 
    }
    
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity;
        if (quantityProperty != null) {
            quantityProperty.set(quantity);
        }
    }
    
    public Double getPrice() { 
        return price; 
    }
    
    public void setPrice(Double price) { 
        this.price = price;
        if (priceProperty != null) {
            priceProperty.set(price);
        }
    }
    
    public String getParameterDescription() {
        return parameterDescription;
    }
    
    public void setParameterDescription(String parameterDescription) {
        this.parameterDescription = parameterDescription;
        if (parametersProperty != null) {
            parametersProperty.set(parameterDescription);
        }
    }
    
    public Double getTotal() {
        return quantity * price;
    }
}
