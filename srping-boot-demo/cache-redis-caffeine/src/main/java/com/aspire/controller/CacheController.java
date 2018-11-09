package com.aspire.controller;

import com.aspire.entity.Person;
import com.aspire.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CacheController {

    @Autowired
    PersonService personService;

    @Autowired
    CacheManager cacheManager;

    @RequestMapping("/save")
    public long put(@RequestBody Person person) {
        Person p = personService.save(person);
        return p.getId();
    }

    @RequestMapping("/query")
    public Person query() {
        return personService.query();
    }

    @RequestMapping("/query_use_id")
    public Person queryById(@RequestBody Person person) {
        return personService.queryById(person);
    }

    @RequestMapping("/evit/{id}")
    public String evit(@PathVariable Long id) {
        personService.remove(id);
        return "ok";
    }

}