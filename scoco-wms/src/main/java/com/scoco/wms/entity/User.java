package com.scoco.wms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 项目名称: 伟明丰查询系统 包名称: com.scoco.wms.entity 类名称: User.java 类描述: 系统用户信息 创建人: sunke 版本号: 1.0.0.0 创建时间:
 * 2018/11/22 15:56
 */
@Entity
@Table(name = "t_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
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