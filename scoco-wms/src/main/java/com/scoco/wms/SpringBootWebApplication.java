package com.scoco.wms;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用启动入口
 *
 * @author sunke
 * @date 2018年12月7日10:42:06
 */
@SpringBootApplication
@MapperScan("com.scoco.wms.dao")
public class SpringBootWebApplication {


    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebApplication.class, args);
    }
}
