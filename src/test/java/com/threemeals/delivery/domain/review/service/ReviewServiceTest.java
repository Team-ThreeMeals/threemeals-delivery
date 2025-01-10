package com.threemeals.delivery.domain.review.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.order.entity.OrderStatus;
import com.threemeals.delivery.domain.order.entity.Order;
import com.threemeals.delivery.domain.order.repository.OrderRepository;
import com.threemeals.delivery.domain.review.dto.request.ReviewRequestDto;
import com.threemeals.delivery.domain.review.dto.response.ReviewResponseDto;
import com.threemeals.delivery.domain.review.entity.Review;
import com.threemeals.delivery.domain.review.exception.ReviewNotAllowedException;
import com.threemeals.delivery.domain.review.repository.ReviewRepository;
import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.user.entity.Role;
import com.threemeals.delivery.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private OrderRepository orderRepository;

	@InjectMocks
	private ReviewService reviewService;

	private static User createMockUser(Long id) {
		User user = User.builder()
			.username("이름")
			.password("123")
			.role(Role.USER)
			.address("주소")
			.build();
		ReflectionTestUtils.setField(user, "id", id);
		return user;
	}

	private static User createMockOwner(Long id) {
		User owner = User.builder()
			.username("사장")
			.password("123")
			.role(Role.STORE_OWNER)
			.address("주소")
			.build();
		ReflectionTestUtils.setField(owner, "id", id);
		return owner;
	}

	private static Store createMockStore(User owner, Long id) {
		Store store = Store.builder()
			.owner(owner)
			.storeName("치킨집")
			.address("주소1")
			.build();
		ReflectionTestUtils.setField(store, "id", id);
		return store;
	}

	public static Order createMockOrder(User user, Store store, Long id, OrderStatus orderStatus) {
		Order order = Order.builder()
			.status(orderStatus)
			.user(user)
			.store(store)
			.build();
		ReflectionTestUtils.setField(order, "id", id);
		return order;
	}


	@Test
	public void 리뷰_등록_성공_테스트 () {
	    // given
		Long userId = 1L;
		Long orderId = 1L;

		User mockUser = createMockUser(userId);
		User mockOwner = createMockOwner(2L);
		Store mockStore = createMockStore(mockOwner, 1L);
		Order mockOrder = createMockOrder(mockUser, mockStore, orderId, OrderStatus.COMPLETED);

		ReviewRequestDto requestDto = new ReviewRequestDto(orderId, 3, "맛있다", "이미지주소");

		when(orderRepository.findOrderByOrderIdAndUserId(requestDto.orderId(), userId)).thenReturn(mockOrder);

		Review mockReview = Review.builder()
			.store(mockStore)
			.user(mockUser)
			.order(mockOrder)
			.content("맛있다")
			.rating(3)
			.build();
		ReflectionTestUtils.setField(mockReview, "createdAt", LocalDateTime.now());

		when(reviewRepository.save(any(Review.class))).thenReturn(mockReview);

	    // when
		ReviewResponseDto responseDto = reviewService.saveReview(requestDto, userId);

	    // then
		assertNotNull(responseDto);
		assertEquals(mockReview.getStore().getStoreName(), responseDto.storeName());
		assertEquals(mockReview.getUser().getUsername(), responseDto.username());
		assertEquals(mockReview.getContent(), responseDto.content());
		assertEquals(mockReview.getRating(), responseDto.rating());
	}

	@Test
	public void 배달완료_전에_리뷰_등록_시_예외발생 () {
		// given
		Long userId = 1L;
		Long orderId = 1L;

		User mockUser = createMockUser(userId);
		User mockOwner = createMockOwner(2L);
		Store mockStore = createMockStore(mockOwner, 1L);
		Order mockOrder = createMockOrder(mockUser, mockStore, orderId, OrderStatus.DELIVERING);

		ReviewRequestDto requestDto = new ReviewRequestDto(orderId, 3, "맛있다", "이미지주소");
		when(orderRepository.findOrderByOrderIdAndUserId(requestDto.orderId(), userId)).thenReturn(mockOrder);

		// when
		ReviewNotAllowedException exception = assertThrows(ReviewNotAllowedException.class, () ->
			reviewService.saveReview(requestDto, userId));

		// then
		assertEquals(ErrorCode.REVIEW_NOT_ALLOWED, exception.getErrorCode());
	}
}
