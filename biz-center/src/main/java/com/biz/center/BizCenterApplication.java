package com.biz.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaServer
@SpringBootApplication
public class BizCenterApplication {

	public static void main(String[] args) {
		SpringApplication.run(BizCenterApplication.class, args);
	}

}
