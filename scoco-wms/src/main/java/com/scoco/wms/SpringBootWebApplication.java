package com.scoco.wms;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * 应用启动入口
 *
 * @author sunke
 * @date 2018年12月7日10:42:06
 */
@SpringBootApplication
//@ServletComponentScan(basePackages = "com.scoco.wms.filter")
public class SpringBootWebApplication {


    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebApplication.class, args);
    }
}
