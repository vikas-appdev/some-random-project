package com.gradlic.fts.erp.repository;

import com.gradlic.fts.erp.domain.LoanEmiSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface LoanEmiScheduleRepository extends JpaRepository<LoanEmiSchedule, Long> {
    Collection<LoanEmiSchedule> findByLoanAccountIdAndStatus(Long id, String Status);
}
