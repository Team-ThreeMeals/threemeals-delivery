package com.threemeals.delivery.domain.user.aspect;


import com.threemeals.delivery.domain.auth.UserPrincipal;
import com.threemeals.delivery.domain.user.annotation.StoreOwnerOnly;
import com.threemeals.delivery.domain.auth.exception.AuthenticationException;
import com.threemeals.delivery.domain.common.exception.AccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j(topic = "AdminAccessAdmin")
@Aspect
@Component
public class StoreOwnerAccessAspect {

    @Before("@annotation(storeOwnerOnly)")
    public void checkAdminAccess(JoinPoint joinPoint, StoreOwnerOnly storeOwnerOnly) {
        Object[] args = joinPoint.getArgs();

        // Check for AuthenticatedUser parameter
        boolean hasAuthenticatedUser = false;
        UserPrincipal userPrincipal = null;

        for (Object arg : args) {
            if (arg instanceof UserPrincipal) {
                hasAuthenticatedUser = true;
                userPrincipal = (UserPrincipal) arg;
                break;
            }
        }

        if (hasAuthenticatedUser == false) {
            throw new AuthenticationException();
        }

        // Check if the user is an admin
        if (userPrincipal.getRole().isStoreOwner() == false) {
            throw new AccessDeniedException();
        }
    }

    private HttpServletRequest getCurrentHttpRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }
}
