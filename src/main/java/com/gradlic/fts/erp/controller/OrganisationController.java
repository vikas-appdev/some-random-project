package com.gradlic.fts.erp.controller;

import com.gradlic.fts.erp.domain.Organisation;
import com.gradlic.fts.erp.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/organisations")
public class OrganisationController {

    @Autowired
    private OrganisationRepository organisationRepository;

    @PostMapping
    public Organisation save(@RequestBody Organisation organisation){
        return organisationRepository.save(organisation);
    }

    @GetMapping
    public List<Organisation> getAllOrganisations(){
        return organisationRepository.findAll();
    }
}
