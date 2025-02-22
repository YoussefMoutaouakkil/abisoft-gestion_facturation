package com.skynet.javafx.model;

import javax.persistence.*;

@Entity
@Table(name = "facture_products")
public class FactureProduct extends SimpleEntity {
    
    @ManyToOne
    @JoinColumn(name = "facture_id")
    private Facture facture;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(name = "parameter_description")
    private String parameterDescription;
    
    public FactureProduct() {}
    
    // Getters and Setters
    public Facture getFacture() { return facture; }
    public void setFacture(Facture facture) { this.facture = facture; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public String getParameterDescription() {
        return parameterDescription;
    }
    
    public void setParameterDescription(String parameterDescription) {
        this.parameterDescription = parameterDescription;
    }
    
    public Double getTotal() {
        return quantity * price;
    }
}
