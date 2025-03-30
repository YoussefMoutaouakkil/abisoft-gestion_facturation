package com.skynet.javafx.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "factures")
public class Facture extends SimpleEntity{

    @Column(name = "numero_facture", unique = true, nullable = false)
    private String numeroFacture;
    
    @Column(name = "date_facture")
    private LocalDateTime dateFacture;
    
    @Column(name = "montant_total")
    private Double montantTotal;
    
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Customer client;
    
    @Column(name = "status")
    private String status;
    
    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<FactureProduct> products = new ArrayList<>();

    private boolean isArchived = false;
    
    public Facture() {}

    
    public String getNumeroFacture() { return numeroFacture; }
    public void setNumeroFacture(String numeroFacture) { this.numeroFacture = numeroFacture; }
    
    public LocalDateTime getDateFacture() { return dateFacture; }
    public void setDateFacture(LocalDateTime dateFacture) { this.dateFacture = dateFacture; }
    
    public Double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(Double montantTotal) { this.montantTotal = montantTotal; }
    
    public Customer getClient() { return client; }
    public void setClient(Customer client) { this.client = client; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<FactureProduct> getProducts() {
        return products;
    }
    
    public void setProducts(List<FactureProduct> products) {
        this.products = products;
    }
    
    public void addProduct(FactureProduct factureProduct) {
        products.add(factureProduct);
        factureProduct.setFacture(this);
        updateMontantTotal();
    }
    
    public void removeProduct(FactureProduct factureProduct) {
        products.remove(factureProduct);
        factureProduct.setFacture(null);
        updateMontantTotal();
    }
    
    private void updateMontantTotal() {
        this.montantTotal = products.stream()
                .mapToDouble(FactureProduct::getTotal)
                .sum();
    }
    
    public String getClientName() {
        return client != null ? client.getFirstname() + " " + client.getLastname() : "";
    }
    public boolean isArchived() {
        return isArchived;
    }
    public void setArchived(boolean isArchived) {
        this.isArchived = isArchived;
    }
}
