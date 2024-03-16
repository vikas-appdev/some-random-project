package com.gradlic.fts.erp.repository;

import com.gradlic.fts.erp.domain.Firm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FirmRepository extends JpaRepository<Firm, Long> {
}
