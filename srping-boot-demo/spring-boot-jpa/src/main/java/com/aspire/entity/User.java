package com.aspire.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Copyright © 2008   卓望公司
 * package: com.aspire.entity
 * fileName: User.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/17 00:13
 */
@Entity
@Table(name = "t_user")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name", length = 32)
    private String name;

    @Column(name = "agee", nullable = true, length = 4)
    private Integer age;

}