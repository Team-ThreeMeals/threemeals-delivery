package com.threemeals.delivery.config;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthUserArgumentResolver());
    }

    @Bean
    FilterRegistrationBean exceptionHandlerFilter() {
        FilterRegistrationBean<Filter> ExceptionHandlerFilterRegisterBean = new FilterRegistrationBean<>();

        ExceptionHandlerFilterRegisterBean.setFilter(new ExceptionHandlerFilter());
        ExceptionHandlerFilterRegisterBean.setOrder(1);
        ExceptionHandlerFilterRegisterBean.addUrlPatterns("/*");

        return ExceptionHandlerFilterRegisterBean;
    }



}
