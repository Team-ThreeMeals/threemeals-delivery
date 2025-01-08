package com.threemeals.delivery.aspect;

import com.threemeals.delivery.domain.order.dto.response.OrderResponseDto;
import com.threemeals.delivery.domain.order.entity.OrderStatus;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class OrderAspect {

    private static final Logger logger = LoggerFactory.getLogger(OrderAspect.class);


    @AfterReturning(pointcut = "execution(* com.threemeals.delivery.domain.order.service.OrderService.createOrder(..))", returning = "orderResponseDto")
    public void logOrderCreation(JoinPoint joinPoint, OrderResponseDto orderResponseDto) {
        logger.info("Order created: timestamp={}, storeId={}, orderId={}",
                LocalDateTime.now(),
                orderResponseDto.getStoreId(),
                orderResponseDto.getOrderId());
    }

    @AfterReturning(pointcut = "execution(* com.threemeals.delivery.domain.order.service.OrderService.updateOrderStatus(..)) && args(orderId, status)", returning = "status")
    public void logOrderStatusUpdate(Long orderId, OrderStatus status) {
        logger.info("Order status updated: timestamp={}, orderId={}, newStatus={}",
                LocalDateTime.now(),
                orderId,
                status);
    }
}
