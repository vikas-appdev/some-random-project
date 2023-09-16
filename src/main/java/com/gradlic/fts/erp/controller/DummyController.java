package com.gradlic.fts.erp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyController {
    @GetMapping("/")
    public String hello(){
        return "Hello world";
    }
}
