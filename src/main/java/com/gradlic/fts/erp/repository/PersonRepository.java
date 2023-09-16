package com.gradlic.fts.erp.repository;

import com.gradlic.fts.erp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<User, Long> {
    List<User> findByFirstNameContaining(String firstName);
    // findByFeeLessThan
}