package com.biz.warning;

import com.biz.base.annotation.EnableBizBoot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableBizBoot
@EnableEurekaClient
@EnableScheduling
@EnableRedisHttpSession
@SpringBootApplication
public class BizWarningApplication {

    public static void main(String[] args) {
        SpringApplication.run(BizWarningApplication.class, args);
    }

}
