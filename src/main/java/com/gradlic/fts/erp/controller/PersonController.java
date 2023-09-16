package com.gradlic.fts.erp.controller;

import com.gradlic.fts.erp.domain.Person;
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
    public Person savePerson(@RequestBody Person person){
        return personRepository.save(person);
    }

    @GetMapping
    public List<Person> getAllPersons(){
        return personRepository.findAll();
    }

    @GetMapping("/{userId}")
    public Person getPersonById(@PathVariable Long userId){
        return personRepository.findById(userId).orElse(null);
    }

    @GetMapping("/find/{name}")
    public List<Person> findPersonsByFirstName(@PathVariable String name){
        return personRepository.findByFirstNameContaining(name);
    }

}
