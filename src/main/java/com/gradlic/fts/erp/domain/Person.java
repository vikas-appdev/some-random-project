package com.gradlic.fts.erp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Person {
    @Id
    @Column(updatable = false, nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    private String userId;
    @NotNull(message = "Firstname can not be null")
    private String firstName;
    @NotNull(message = "Last name can not be null")
    private String lastName;
    private String username;
    private String password;
    @NotNull(message = "Email can not be null")
    @Email(message = "Invalid email, Please enter a valid email")
    private String email; // UNIQUE
    private String profileImageUrl; // CDN flaticon avtar 512 149
    private LocalDateTime lastLoginDate;
    private LocalDateTime lastLoginDateDisplay;
    private LocalDateTime createAt;
    private String role;
    private boolean isActive; // enabled
    private boolean isNotLocked;
    private String userNFCIdCardNumber;
    private String aadhaarNumber;
    private String mobileNumber;
    private String title;
    private String bio;
    private boolean isUsingMFA; // Multi factor authentication

    @Embedded
    private Address address;

    @ManyToMany
    @JoinTable(name = "PERSON_ORGANISATION", joinColumns = {
            @JoinColumn(name = "person_id", referencedColumnName = "id")
    },
    inverseJoinColumns = {
            @JoinColumn(name = "organisation_id", referencedColumnName = "id")
    })
    @JsonIgnoreProperties("persons")
    private Set<Organisation> organisations;


}
