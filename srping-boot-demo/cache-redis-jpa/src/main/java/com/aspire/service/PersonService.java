package com.aspire.service;

import com.aspire.entity.Person;

import java.util.List;


public interface PersonService {
    Person save(Person person);

    void remove(Person person);

    Person findOne(Person person);

}
