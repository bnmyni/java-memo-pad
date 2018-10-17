package com.scoco.demo.lombok;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * 用户信息
 * Copyright © 2008 scoco
 * package: com.scoco.demo.lombok
 * fileName: User.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/06 16:49
 */
@Data
@ToString(includeFieldNames = false)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class User {

    private Integer id;
    @NonNull
    private String name;

    private String department;
}