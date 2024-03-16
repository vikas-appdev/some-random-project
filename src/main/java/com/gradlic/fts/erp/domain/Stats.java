package com.gradlic.fts.erp.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Stats {
    private int totalCustomers;
    private int totalInvoices;
    private double totalBilled;
    private int todaysCollection;
    private int totalRunningLoanAccount;
    private int totalDue;

}
