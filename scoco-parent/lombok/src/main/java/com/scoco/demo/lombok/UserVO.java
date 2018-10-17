package com.scoco.demo.lombok;

import lombok.Value;

/**
 * 声明一个value对象, 这里使用到lombok的@Value注解，
 * 方法只会为属性生成get方法，不会生成set方法,且会自动生成一个包含所有参数的构造器，即所有的属性必须初始化
 * Copyright © 2028  scoco.com
 * package: com.scoco.demo.lombok
 * fileName: UserVO.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/06 17:04
 */
@Value
public class UserVO {

    private String name;

    private String sex;
}