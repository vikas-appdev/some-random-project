package com.gradlic.fts.erp.repository;

import com.gradlic.fts.erp.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findByFirstNameContaining(String firstName);
    // findByFeeLessThan
}