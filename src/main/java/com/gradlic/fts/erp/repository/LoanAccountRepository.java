package com.gradlic.fts.erp.repository;

import com.gradlic.fts.erp.domain.LoanAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanAccountRepository extends JpaRepository<LoanAccount, Long> {
}
