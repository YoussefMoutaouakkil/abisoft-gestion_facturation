package com.skynet.javafx.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Lob;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "company_info")
public class CompanyInfo extends SimpleEntity {
    private String raisonSociale;
    private String adresse;
    private String rc;
    private String ice;
    
    @Lob
    private byte[] logo;
    private String telephone;
}
