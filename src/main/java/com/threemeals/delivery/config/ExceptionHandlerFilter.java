package com.threemeals.delivery.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.config.error.ErrorResponse;
import com.threemeals.delivery.domain.common.exception.BaseException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.threemeals.delivery.config.error.ErrorCode.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class ExceptionHandlerFilter implements Filter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (BaseException e) { // Get JWT Exception
            sendErrorResponse(httpServletResponse, e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(httpServletResponse, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.getMessage());
            log.error("error");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode, String errorMessage) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, errorMessage);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}
