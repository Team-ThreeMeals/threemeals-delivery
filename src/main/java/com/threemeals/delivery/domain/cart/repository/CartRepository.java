package com.threemeals.delivery.domain.cart.repository;

import com.threemeals.delivery.domain.cart.entity.Cart;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CartRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CART_KEY_PREFIX = "cart:";

    public CartRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveCart(Long userId, Cart cart) {
        String cartKey = CART_KEY_PREFIX + userId; // Redis 키 생성
        cart.setId(userId); // Cart의 ID로 설정
        redisTemplate.opsForValue().set(cartKey, cart);
    }

    public Optional<Cart> findCart(Long userId) {
        Object cart = redisTemplate.opsForValue().get(CART_KEY_PREFIX + userId);
        return Optional.ofNullable((Cart) cart);
    }

    public void deleteCart(Long userId) {
        redisTemplate.delete(CART_KEY_PREFIX + userId);
    }
    public void orderafterdeleteCart(Long userId) {
        redisTemplate.delete("cart:" + userId);
    }
}
