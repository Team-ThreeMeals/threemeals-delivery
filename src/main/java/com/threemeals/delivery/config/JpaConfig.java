package com.threemeals.delivery.config;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.*;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Configuration
@EnableJpaAuditing
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO) // 페이징시 파라미터 간소화
public class JpaConfig {

}
