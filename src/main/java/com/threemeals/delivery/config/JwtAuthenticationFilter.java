package com.threemeals.delivery.config;

import static com.threemeals.delivery.config.error.ErrorCode.*;
import static com.threemeals.delivery.config.util.Token.*;
import static com.threemeals.delivery.config.util.Url.*;
import static org.springframework.util.StringUtils.*;

import java.io.IOException;

import com.threemeals.delivery.config.jwt.TokenProvider;
import com.threemeals.delivery.domain.common.exception.NotFoundException;
import com.threemeals.delivery.domain.user.entity.Role;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements Filter {

    private final TokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest httpRequest,
                         ServletResponse httpResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) httpRequest;
        HttpServletResponse response = (HttpServletResponse) httpResponse;

        if (isIncludedInWhiteList(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = request.getHeader(AUTHORIZATION_HEADER);
        validateToken(accessToken);

        // Wrapping authentication Principal
        Long userId = tokenProvider.getUserId(accessToken);
        Role role = tokenProvider.getRole(accessToken);

        request.setAttribute("userId", userId);
        request.setAttribute("role", role);

        filterChain.doFilter(request, response);
    }


    private void validateToken(String accessToken) throws IOException {

        if (hasText(accessToken) == false) {
            log.error("Missing JWT token. Sending error response");
            throw new NotFoundException(TOKEN_NOT_FOUND);
        }

        tokenProvider.validateToken(accessToken);
    }

}
