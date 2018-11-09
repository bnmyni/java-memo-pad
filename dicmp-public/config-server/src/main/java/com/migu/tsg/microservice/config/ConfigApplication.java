package com.migu.tsg.microservice.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;



@EnableConfigServer
@SpringBootApplication
public class ConfigApplication {
	/**
	 * @param args main方法
	 */
	public static void main(final String[] args) {
		SpringApplication.run(ConfigApplication.class, args);
	}
}
