package com.gradlic.fts.erp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Entity
public class LoanEmiSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate paymentDate;
    private int amount;
    private String status; // PAID UNPAID

    @OneToOne(mappedBy = "loanEmiSchedule")
    private LoanEmiPayments loanEmiPayments;
    @ManyToOne
    @JsonIgnore
    private LoanAccount loanAccount;
}
