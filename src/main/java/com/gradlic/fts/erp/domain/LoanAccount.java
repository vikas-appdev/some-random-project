package com.gradlic.fts.erp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class LoanAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String loanType; // DAILY_WAGE INTEREST EMI_WITH_INTEREST
    private LocalDate accountOpeningDate;
    private int totalAmount;
    private int processingCharge;
    private int numberOfEmi;
    private String emiType; // DAILY MONTHLY
    private String accountStatus; // RUNNING PRE_CLOSED CLOSED

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "loanAccount", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Collection<LoanEmiSchedule> loanEmiSchedules;

    @OneToMany(mappedBy = "loanAccount", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Collection<LoanEmiPayments> loanEmiPayments;


    @ManyToOne
    @JsonIgnore
    private Customer customer;

}
