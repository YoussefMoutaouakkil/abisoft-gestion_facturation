package com.skynet.javafx.model;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Entity
@Table(name = "products")
public class Product extends SimpleEntity {
    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private Double price;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ProductParameter> parameters = new ArrayList<>();

    private transient StringProperty nameProperty;

    public Product() {
    }

    public Product(String name, String description, Double price, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (nameProperty != null) {
            nameProperty.set(name);
        }
    }

    public StringProperty nameProperty() {
        if (nameProperty == null) {
            nameProperty = new SimpleStringProperty(getName());
        }
        return nameProperty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getCategoryName() {
        return category != null ? category.getName() : "";
    }

    public List<ProductParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ProductParameter> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(ProductParameter parameter) {
        parameters.add(parameter);
        parameter.setProduct(this);
    }

    public void removeParameter(ProductParameter parameter) {
        parameters.remove(parameter);
        parameter.setProduct(null);
    }

    public Integer getQuantity() {
        return parameters.stream()
                .mapToInt(ProductParameter::getStockQuantity)
                .sum();
    }
}
