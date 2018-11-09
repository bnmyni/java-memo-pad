package com.aspire.service.impl;

import com.aspire.cache.LayCacheable;
import com.aspire.cache.setting.FirstCacheSetting;
import com.aspire.entity.Person;
import com.aspire.repository.PersonRepository;
import com.aspire.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {
    private static final Logger logger = LoggerFactory.getLogger(PersonServiceImpl.class);

    @Autowired
    PersonRepository personRepository;

    @Override
    @CachePut(value = "user", key = "#person.id")
    public Person save(Person person) {
        logger.info("缓存人员信息,key=people1:{}", person.getId());
        return personRepository.save(person);
    }

    @Override
    @CacheEvict(value = "user", key = "#id")
    public void remove(Long id) {
        logger.info("删除了id、key为" + id + "的数据缓存");
    }

    @Override
    @Cacheable(value = "people")
    public Person query() {
        logger.info("缓存人员信息,key=people1:6");
       return personRepository.findOne(6L);
    }

    /**
     * 使用一级缓存
     */
    @Override
    @Cacheable(value = "user", key = "#person.id", sync = true)
    @LayCacheable(ccc = @Cacheable(value = "user", key = "#person.id", sync = true))
    public Person queryById(Person person) {
        Person p = personRepository.findOne(person.getId());
        if (p != null) {
            logger.info("为id、key为:" + p.getId() + "数据做了缓存");
        }
        return p;
    }

}
