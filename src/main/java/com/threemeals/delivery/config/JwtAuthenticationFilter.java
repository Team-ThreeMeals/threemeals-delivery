package com.threemeals.delivery.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.threemeals.delivery.config.jwt.TokenProvider;
import com.threemeals.delivery.domain.common.exception.NotFoundException;
import com.threemeals.delivery.domain.user.entity.Role;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static com.threemeals.delivery.config.error.ErrorCode.TOKEN_NOT_FOUND;
import static com.threemeals.delivery.config.util.Token.AUTHORIZATION_HEADER;
import static com.threemeals.delivery.config.util.Url.isIncludedInWhiteList;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements Filter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest httpRequest,
                         ServletResponse httpResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) httpRequest;
        HttpServletResponse response = (HttpServletResponse) httpResponse;

        log.debug("Processing authorization for request: {}", request.getRequestURI());

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

        log.debug("JWT token is valid for request.");
    }

}
