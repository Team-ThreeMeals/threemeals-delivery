package com.threemeals.delivery.domain.cart;

import com.threemeals.delivery.domain.auth.UserPrincipal;
import com.threemeals.delivery.domain.cart.dto.request.AddCartItemRequestDto;
import com.threemeals.delivery.domain.cart.dto.request.AddCartRequestDto;
import com.threemeals.delivery.domain.cart.dto.response.CartResponseDto;
import com.threemeals.delivery.domain.cart.entity.Cart;
import com.threemeals.delivery.domain.cart.service.CartService;
import com.threemeals.delivery.domain.cart.repository.CartRepository;
import com.threemeals.delivery.domain.menu.entity.Menu;
import com.threemeals.delivery.domain.menu.repository.MenuRepository;
import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.store.repository.StoreRepository;
import com.threemeals.delivery.domain.user.entity.User;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private StoreRepository storeRepository;

    public CartServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddToCart() {
        // Given
        Long userId = 1L;
        Long storeId = 2L;
        Long menuId = 3L;
        User user = new User(userId, "test@example.com", "Test User");
        Store store = new Store(storeId, "Test Store", 10000);
        Menu menu = new Menu(menuId, "Test Menu", 5000);

        AddCartRequestDto requestDto = new AddCartRequestDto();
        requestDto.setStoreId(storeId);
        AddCartItemRequestDto itemDto = new AddCartItemRequestDto();
        itemDto.setMenuId(menuId);
        itemDto.setQuantity(2);
        requestDto.setCartItems(List.of(itemDto));

        when(cartRepository.findCart(userId)).thenReturn(Optional.empty());
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        // When
        cartService.addToCart(new UserPrincipal(userId, null), requestDto);

        // Then
        verify(cartRepository).saveCart(eq(userId), any(Cart.class));
    }

    @Test
    void testGetCart() {
        // Given
        Long userId = 1L;
        Cart cart = new Cart();
        when(cartRepository.findCart(userId)).thenReturn(Optional.of(cart));

        // When
        CartResponseDto response = cartService.getCart(new UserPrincipal(userId, null));

        // Then
        assertNotNull(response);
    }
}
