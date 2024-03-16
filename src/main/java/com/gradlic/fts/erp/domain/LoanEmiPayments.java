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

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Entity
public class LoanEmiPayments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate paymentDate;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private int amount;
    private String receivedBy;
    private long receivedByUserId;
    private String remarks; // ON_TIME_PAYMENT LATE_PAYMENT PRE_PAID
    @ManyToOne
    @JsonIgnore
    private LoanAccount loanAccount;
    @OneToOne
    @JsonIgnore
    private LoanEmiSchedule loanEmiSchedule;
}
