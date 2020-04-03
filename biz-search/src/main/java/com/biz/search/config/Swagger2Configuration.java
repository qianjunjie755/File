package com.biz.search.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@Profile({"dev", "uat"})
@EnableSwagger2
public class Swagger2Configuration {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .pathMapping("/")
                .apiInfo(new ApiInfoBuilder()
                        .title("信息查询RESTful API")
                        .description("API描述")
                        .version("1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.biz.search.controller"))
                .paths(PathSelectors.any())
                .build();
    }
}
