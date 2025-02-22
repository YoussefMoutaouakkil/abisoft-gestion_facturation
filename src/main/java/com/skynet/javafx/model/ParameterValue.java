package com.skynet.javafx.model;

import javax.persistence.*;

@Entity
@Table(name = "parameter_values")
public class ParameterValue extends SimpleEntity {
    
    @Column(nullable = false)
    private String value;
    
    @Column(nullable = false)
    private Integer stockQuantity;
    
    @ManyToOne
    private ProductParameter parameter;
    
    // Getters and Setters
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public ProductParameter getParameter() {
        return parameter;
    }
    
    public void setParameter(ProductParameter parameter) {
        this.parameter = parameter;
    }
    
    @Override
    public String toString() {
        return value + " (Stock: " + stockQuantity + ")";
    }
}
