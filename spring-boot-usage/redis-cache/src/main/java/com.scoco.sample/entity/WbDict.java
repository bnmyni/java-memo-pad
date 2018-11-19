package com.scoco.sample.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 数据字典实体对象
 * Copyright © 2008   卓望公司
 * package: com.aspire.dicmp.sample.cache.entity
 * fileName: WbDict.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/11/08 16:49
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WbDict implements Serializable {

    private static final long serialVersionUID = -2830939627085135084L;

    @Id
    private String id;

    private String dictType;

    private String code;

    private String name;

    private char status;

    private String description;

    private Long createUser;

    private String createUserName;

    private Date createDate;

    private Date lastUpdateDate;
}