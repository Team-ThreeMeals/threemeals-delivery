package com.threemeals.delivery.config;

import com.threemeals.delivery.config.jwt.TokenProvider;
import com.threemeals.delivery.domain.oauth.service.OAuthUserService;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final OAuthUserService oAuthUserService;

    @Bean
    public JwtAuthenticationFilter  jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)  // JWT 필터 추가
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**", "/css/**", "/images/**", "/js/**").permitAll()
                        .requestMatchers("/oauth/**", "/oauth/login/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuthUserService))
                        .defaultSuccessUrl("/oauth/login/success", true)//리다이렉트 -> 홈 페이지
                        .failureUrl("/oauth/login/failure")//리다이렉트 -> 로그인 페이지
                );

        return http.build();
    }
}
