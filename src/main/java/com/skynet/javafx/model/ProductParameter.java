package com.skynet.javafx.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_parameters")
public class ProductParameter extends SimpleEntity {
    
    @Column(nullable = false)
    private String name;
    
    @OneToMany(mappedBy = "parameter", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<ParameterValue> values = new ArrayList<>();
    
    @ManyToOne
    private Product product;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<ParameterValue> getValues() {
        return values;
    }

    public void setValues(List<ParameterValue> values) {
        this.values = values;
    }

    public void addValue(String value, Integer stockQuantity) {
        ParameterValue paramValue = new ParameterValue();
        paramValue.setValue(value);
        paramValue.setStockQuantity(stockQuantity);
        paramValue.setParameter(this);
        values.add(paramValue);
    }

    public Integer getStockQuantity() {
        return values.stream()
                .mapToInt(ParameterValue::getStockQuantity)
                .sum();
    }
}