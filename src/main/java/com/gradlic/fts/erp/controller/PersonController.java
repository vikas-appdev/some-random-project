package com.gradlic.fts.erp.controller;

import com.gradlic.fts.erp.domain.User;
import com.gradlic.fts.erp.repository.OrganisationRepository;
import com.gradlic.fts.erp.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/persons")
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @PostMapping
    public User savePerson(@RequestBody User user){
        return personRepository.save(user);
    }

    @GetMapping
    public List<User> getAllPersons(){
        return personRepository.findAll();
    }

    @GetMapping("/{userId}")
    public User getPersonById(@PathVariable Long userId){
        return personRepository.findById(userId).orElse(null);
    }

    @GetMapping("/find/{name}")
    public List<User> findPersonsByFirstName(@PathVariable String name){
        return personRepository.findByFirstNameContaining(name);
    }

}
