package com.threemeals.delivery.domain.cart.repository;

import com.threemeals.delivery.domain.cart.entity.Cart;
import com.threemeals.delivery.domain.cart.entity.CartItem;
import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.menu.entity.Menu;
import com.threemeals.delivery.domain.user.entity.User;
import com.threemeals.delivery.domain.user.entity.Role;
import com.threemeals.delivery.domain.menu.entity.Category;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Test
    void testSaveAndFindCart() {
        // Given
        User owner = User.builder()
                .username("owner1")
                .email("owner1@test.com")
                .password("password1")
                .role(Role.STORE_OWNER)
                .address("Owner Address")
                .profileImgUrl("owner1.png")
                .build();

        Store store = Store.builder()
                .owner(owner)
                .storeName("Test Store")
                .storeProfileImgUrl("test-profile.png")
                .address("123 Test Address")
                .openingTime(LocalTime.of(9, 0))
                .closingTime(LocalTime.of(21, 0))
                .deliveryTip(3000)
                .minOrderPrice(10000)
                .build();

        Menu menu = Menu.builder()
                .category(Category.KOREAN)
                .menuName("Test Menu")
                .description("Delicious Test Menu")
                .price(15000)
                .menuImgUrl("test-menu.png")
                .build();
        menu.setStore(store);

        CartItem cartItem = CartItem.builder()
                .menu(menu)
                .quantity(3)
                .build();

        Cart cart = Cart.builder()
                .userId(1L)
                .store(store)
                .totalPrice(45000)
                .cartItems(Arrays.asList(cartItem))
                .build();

        // When
        cartRepository.saveCart(1L, cart);

        // Then
        Optional<Cart> retrievedCart = cartRepository.findCart(1L);
        assertThat(retrievedCart).isPresent();
        assertThat(retrievedCart.get().getUserId()).isEqualTo(1L);
        assertThat(retrievedCart.get().getStore().getStoreName()).isEqualTo("Test Store");
        assertThat(retrievedCart.get().getCartItems()).hasSize(1);
        assertThat(retrievedCart.get().getCartItems().get(0).getMenu().getMenuName()).isEqualTo("Test Menu");
    }
}
