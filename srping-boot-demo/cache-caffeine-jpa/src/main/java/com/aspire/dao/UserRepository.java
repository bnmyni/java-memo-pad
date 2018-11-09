package com.aspire.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aspire.entity.User;

/**
 * Copyright © 2008   卓望公司
 * package: com.aspire.dao
 * fileName: UserRepository.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/17 00:19
 */
public interface  UserRepository extends JpaRepository<User, Long> {
}