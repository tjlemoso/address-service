package org.acme.com.model;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "address")
@Getter
@Setter
public class Address {

  @Id
  @Column(name = "addressId")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long addressId;

  @Column(name = "createData")
  private LocalDate createData;

  @Column(name = "address")
  private String address;

  @Column(name = "address2")
  private String address2;

  @Column(name = "number")
  private int number;

  @Column(name = "city")
  private String city;

  @Column(name = "country")
  private String country;

  @Column(name = "zipCode")
  private String zipCode;
  
}
