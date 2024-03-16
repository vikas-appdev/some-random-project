package com.gradlic.fts.erp.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gradlic.fts.erp.enumeration.FirmStatus;
import com.gradlic.fts.erp.enumeration.FirmType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Entity
public class Firm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firmName;
    private String tagLine;
    private String email;
    private String phone;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDate renewalDate;
    private LocalDate expiryDate;
    private boolean locked;
    @Enumerated(EnumType.STRING)
    private FirmStatus status;
    private boolean online;
    @Enumerated(EnumType.STRING)
    private FirmType firmType;
    @Embedded
    private Address address;

    @OneToMany(mappedBy = "firm", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Collection<Customer> customers;

}
