package com.skynet.javafx.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class Customer extends SimpleEntity {

  @Column(nullable = false)
  private String firstname;

  @Column(nullable = false)
  private String lastname;

  @Column
  private String address;

  @Column
  private String email;
  @Column
  private String ICE;
  @Column
  private String type;
  @Column
  private String tel;

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
  public String getICE() {
    return ICE;
  }
  public void setICE(String ICE) {
    this.ICE = ICE;
  }
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getTel() {
    return tel;
  }
  public void setTel(String tel) {
    this.tel = tel;
  }

}
