package com.biz.warning.config;

import com.biz.warning.interceptor.POCInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 自定义配置静态资源映射-不会覆盖Spring Boot的默认配置，/public /resources 等默认配置依然可以使用
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private POCInterceptor pocInterceptor;

    //手动配置静态资源路径
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(false).
                setUseTrailingSlashMatch(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(pocInterceptor).excludePathPatterns("/task/list", "/task/entity");
    }
}