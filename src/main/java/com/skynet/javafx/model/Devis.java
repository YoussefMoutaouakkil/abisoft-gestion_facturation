package com.skynet.javafx.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Entity
@Component
@Table(name = "devis")
public class Devis extends SimpleEntity {
    
    // Remove this ID declaration as it's already in SimpleEntity
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    // private Long id;

    @Column(name = "numero_devis", nullable = false, unique = true)
    private String numeroDevis;
    
    @Column(name = "date_devis", nullable = false)
    private LocalDateTime dateDevis;
    
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Customer client;
    
    @Column(nullable = false)
    private String status;
    
    @Column(name = "montant_total")
    private Double montantTotal = 0.0;
    
    @OneToMany(mappedBy = "devis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DevisProduct> products = new ArrayList<>();
    
    @Column(name = "validity_period")
    private Integer validityPeriod = 30; // Default validity period in days
    
    @Column
    private String notes;

    // Default constructor required by JPA
    public Devis() {}

    // Getters and Setters
    public String getNumeroDevis() {
        return numeroDevis;
    }

    public void setNumeroDevis(String numeroDevis) {
        this.numeroDevis = numeroDevis;
    }

    public LocalDateTime getDateDevis() {
        return dateDevis;
    }

    public void setDateDevis(LocalDateTime dateDevis) {
        this.dateDevis = dateDevis;
    }

    public Customer getClient() {
        return client;
    }

    public void setClient(Customer client) {
        this.client = client;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(Double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public List<DevisProduct> getProducts() {
        return products;
    }

    public void setProducts(List<DevisProduct> products) {
        this.products = products;
    }

    public Integer getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(Integer validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Helper methods
    public void addProduct(DevisProduct product) {
        products.add(product);
        product.setDevis(this);
        updateTotal();
    }

    public void removeProduct(DevisProduct product) {
        products.remove(product);
        product.setDevis(null);
        updateTotal();
    }

    private void updateTotal() {
        this.montantTotal = products.stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();
    }
}
