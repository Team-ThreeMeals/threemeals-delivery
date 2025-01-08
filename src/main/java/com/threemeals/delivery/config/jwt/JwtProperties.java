package com.threemeals.delivery.config.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Setter 없으면 에러남.
 * `Spring Boot`는 `@ConfigurationProperties`를 사용할 때, 기본적으로 `setter`를 이용한 프로퍼티 바인딩을 수행
 */
@Setter
@Getter
@NoArgsConstructor
@Component
@ConfigurationProperties("jwt")
public class JwtProperties {

    private String issuer;
    private String secretKey;
}