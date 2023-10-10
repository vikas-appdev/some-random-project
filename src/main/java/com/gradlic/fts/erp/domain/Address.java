package com.gradlic.fts.erp.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Embeddable
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String doorNumber;
    private String street;
    private String locality;
    private String city;
    private String state;
    private int pincode;
    private String country;
    private double latitude;
    private double longitude;


}
