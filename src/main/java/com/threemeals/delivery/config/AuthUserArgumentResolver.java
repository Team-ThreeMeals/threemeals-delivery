package com.threemeals.delivery.config;


import com.threemeals.delivery.domain.auth.UserPrincipal;
import com.threemeals.delivery.domain.auth.annotation.Authentication;
import com.threemeals.delivery.domain.common.exception.InvalidRequestException;
import com.threemeals.delivery.domain.user.entity.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.threemeals.delivery.config.error.ErrorCode.INVALID_ROLE;

public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation = parameter.getParameterAnnotation(Authentication.class) != null;
        boolean isValidParameterType = parameter.getParameterType().equals(UserPrincipal.class);

        // `@Authentication` should be used with `AuthenticatedUser`
        if (hasAnnotation == false || isValidParameterType == false) {
            throw new InvalidRequestException(INVALID_ROLE);
        }

        return true;
    }


    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        Long userId = (Long) request.getAttribute("userId");
        Role role = Role.of(String.valueOf(request.getAttribute("role")));

        return UserPrincipal.fromRequest(userId, role);
    }
}
