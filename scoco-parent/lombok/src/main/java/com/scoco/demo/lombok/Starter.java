package com.scoco.demo.lombok;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 用于测试说明lombok的其他注解
 * Copyright © 2008   卓望公司
 * package: com.scoco.demo.lombok
 * fileName: Starter.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/06 16:57
 */
@Log
public class Starter {

    private static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @SneakyThrows
    public static void main(String[] args) {

        log.info("通过lombok注解@Log声明一个log");

        UserVO vo = new UserVO("aaa", null);
        log.info(vo.getName());

        log.info("使用lombok@Synchronized 格式化时间:" + synDateFormat(new Date()));

        log.info(byte2String(new byte[]{'c', 't'}));

        // 使用  @Cleanup 后 会自动的生成 finally reader.close
        @Cleanup BufferedReader reader  = new BufferedReader(new FileReader(new File("pom.xml")));
        log.info(reader.readLine());
    }

    @Synchronized
    private static String synDateFormat(Date date) {
        return sdf.format(date);
    }


    @SneakyThrows
    private static String byte2String(byte[] bytes) {
        return new String(bytes, "utf-8");
    }

}