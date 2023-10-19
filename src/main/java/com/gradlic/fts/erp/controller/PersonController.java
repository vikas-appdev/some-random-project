package com.gradlic.fts.erp.controller;

//@RestController
//@RequestMapping("/api/v1/persons")
//public class PersonController {
//
//    @Autowired
//    private PersonRepository personRepository;
//
//    @Autowired
//    private OrganisationRepository organisationRepository;
//
//    @PostMapping
//    public User savePerson(@RequestBody User user){
//        return personRepository.save(user);
//    }
//
//    @GetMapping
//    public List<User> getAllPersons(){
//        return personRepository.findAll();
//    }
//
//    @GetMapping("/{userId}")
//    public User getPersonById(@PathVariable Long userId){
//        return personRepository.findById(userId).orElse(null);
//    }
//
//    @GetMapping("/find/{name}")
//    public List<User> findPersonsByFirstName(@PathVariable String name){
//        return personRepository.findByFirstNameContaining(name);
//    }
//
//}
