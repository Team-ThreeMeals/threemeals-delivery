package com.threemeals.delivery.config;

import com.threemeals.delivery.config.util.YamlPropertySourceFactory;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource(value = "classpath:myInfo.yml", factory = YamlPropertySourceFactory.class)
public class MyInfoConfig {
    @Value("${naver.oauth.client-id}")
    private String clientId;

    @Value("${naver.oauth.client-secret}")
    private String clientSecret;

    @Value("${naver.oauth.redirect-uri}")
    private String redirectUri;

    @Value("${db.user}")
    private String user;

    @Value("${db.name}")
    private String name;

    @Value("${db.pw}")
    private String pw;

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private String port;
}


