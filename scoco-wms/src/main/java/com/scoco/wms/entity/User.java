package com.scoco.wms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author sunke
 * @date 2018/12/19
 * @version 1.0.0.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Integer id;

    private String userName;

    private String realName;

    private String password;

    private String status;

    private String sex;

    private String telephone;

    private String mobile;

    private String email;

    private Date expireDate;

    private Date passwordExpireDate;

    private Date lockDate;

    private String domain;

    private String createUser;

    private String createUserName;

    private Date createDate;

    private Date lastUpdateDate;
}