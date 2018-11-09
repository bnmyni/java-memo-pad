package com.aspire.controller;

import com.aspire.entity.Person;
import com.aspire.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
/**
 * Copyright © 2018-2028 aspire Inc. All rights reserved.
 * package: com.aspire.controller
 * fileName: CacheController.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/18 18:00
 */
@RestController
public class CacheController {

    @Autowired
    PersonService personService;

    @Autowired
    CacheManager cacheManager;

    @RequestMapping("/put")
    public long put(@RequestBody Person person) {
        Person p = personService.save(person);
        return p.getId();
    }

    @RequestMapping("/query")
    public Person query(@RequestBody Person person) {

        return personService.findOne(person);
    }


    @RequestMapping("/evit")
    public String evit(@RequestBody Person person) {
        personService.remove(person);
        return "ok";
    }

}