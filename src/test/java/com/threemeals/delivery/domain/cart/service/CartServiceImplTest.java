package com.threemeals.delivery.domain.cart.service;

import com.threemeals.delivery.domain.cart.dto.request.*;
import com.threemeals.delivery.domain.cart.dto.response.CartResponseDto;
import com.threemeals.delivery.domain.cart.entity.Cart;
import com.threemeals.delivery.domain.cart.entity.CartItem;
import com.threemeals.delivery.domain.cart.entity.CartItemOption;
import com.threemeals.delivery.domain.cart.repository.CartRepository;
import com.threemeals.delivery.domain.menu.entity.Menu;
import com.threemeals.delivery.domain.menu.entity.MenuOption;
import com.threemeals.delivery.domain.menu.repository.MenuOptionRepository;
import com.threemeals.delivery.domain.menu.repository.MenuRepository;
import com.threemeals.delivery.domain.order.entity.Order;
import com.threemeals.delivery.domain.order.entity.OrderItem;
import com.threemeals.delivery.domain.order.repository.OrderItemOptionRepository;
import com.threemeals.delivery.domain.order.repository.OrderItemRepository;
import com.threemeals.delivery.domain.order.repository.OrderRepository;
import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.store.repository.StoreRepository;
import com.threemeals.delivery.domain.user.entity.User;
import com.threemeals.delivery.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CartServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderItemOptionRepository orderItemOptionRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuOptionRepository menuOptionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCart_Success() {
        // Given
        Cart cart = mock(Cart.class);
        when(cartRepository.findCart(1L)).thenReturn(Optional.of(cart));
        when(cart.getId()).thenReturn(1L);
        when(cart.getCartItems()).thenReturn(Collections.emptyList());

        // When
        CartResponseDto response = cartService.getCart(1L);

        // Then
        assertThat(response.getCartItems()).isEmpty();
        verify(cartRepository).findCart(1L);
    }

    @Test
    void testGetCart_EmptyCart() {
        // Given
        when(cartRepository.findCart(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> cartService.getCart(1L));
    }

    @Test
    void testAddToCart_Success() {
        // Given
        // 장바구니 항목 옵션 생성
        AddCartItemOptionRequestDto option1 = new AddCartItemOptionRequestDto(101L, 500);
        List<AddCartItemOptionRequestDto> options = List.of(option1);

        // 장바구니 항목 생성
        AddCartItemRequestDto cartItem = new AddCartItemRequestDto(1L, 2, options);
        List<AddCartItemRequestDto> cartItems = List.of(cartItem);

        // AddCartRequestDto 생성
        AddCartRequestDto request = new AddCartRequestDto(1L, cartItems);

        // Mock 데이터 설정
        Store store = mock(Store.class);
        Menu menu = mock(Menu.class);
        MenuOption menuOption = mock(MenuOption.class);

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        when(menuOptionRepository.findById(101L)).thenReturn(Optional.of(menuOption));
        when(cartRepository.findCart(1L)).thenReturn(Optional.empty());

        // When
        cartService.addToCart(1L, request);

        // Then
        verify(storeRepository).findById(1L);
        verify(menuRepository).findById(1L);
        verify(menuOptionRepository).findById(101L);
        verify(cartRepository).saveCart(eq(1L), any(Cart.class));
    }


    @Test
    void testDeleteCartItem_Success() {
        // Given
        Cart cart = mock(Cart.class);
        CartItem cartItem = mock(CartItem.class);

        when(cartRepository.findCart(1L)).thenReturn(Optional.of(cart));
        when(cart.getCartItems()).thenReturn(Collections.singletonList(cartItem));
        when(cartItem.getMenu()).thenReturn(mock(Menu.class));
        when(cartItem.getMenu().getId()).thenReturn(1L);

        // When
        cartService.deleteCartItem(1L, 1L);

        // Then
        verify(cart).removeCartItem(cartItem);
        verify(cartRepository).saveCart(eq(1L), eq(cart));
    }

    @Test
    void testClearCart_Success() {
        // When
        cartService.clearCart(1L);

        // Then
        verify(cartRepository).deleteCart(1L);
    }

    @Test
    void testCreateOrderFromCart_Success() {
        // Given
        Long userId = 1L;
        String deliveryAddress = "Test Address";

        // Mock 데이터 생성
        Store store = mock(Store.class);
        when(store.getId()).thenReturn(1L);

        User user = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Cart cart = mock(Cart.class);
        when(cart.getStore()).thenReturn(store);
        when(cart.getTotalPrice()).thenReturn(10000);
        when(cart.getCartItems()).thenReturn(Collections.emptyList());
        when(cartRepository.findCart(userId)).thenReturn(Optional.of(cart));

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

        // When
        cartService.createOrderFromCart(userId, deliveryAddress);

        // Then
        verify(cartRepository).findCart(userId);
        verify(userRepository).findById(userId);
        verify(storeRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

}
