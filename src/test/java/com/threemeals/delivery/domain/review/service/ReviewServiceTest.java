package com.threemeals.delivery.domain.review.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.AccessException;
import org.springframework.test.util.ReflectionTestUtils;

import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.AccessDeniedException;
import com.threemeals.delivery.domain.order.entity.OrderStatus;
import com.threemeals.delivery.domain.order.entity.Order;
import com.threemeals.delivery.domain.order.repository.OrderRepository;
import com.threemeals.delivery.domain.review.dto.request.ReviewCommentRequestDto;
import com.threemeals.delivery.domain.review.dto.request.ReviewRequestDto;
import com.threemeals.delivery.domain.review.dto.response.ReviewCommentResponseDto;
import com.threemeals.delivery.domain.review.dto.response.ReviewResponseDto;
import com.threemeals.delivery.domain.review.entity.Review;
import com.threemeals.delivery.domain.review.entity.ReviewComment;
import com.threemeals.delivery.domain.review.exception.ReviewNotAllowedException;
import com.threemeals.delivery.domain.review.repository.ReviewCommentRepository;
import com.threemeals.delivery.domain.review.repository.ReviewRepository;
import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.store.exception.StoreAccessException;
import com.threemeals.delivery.domain.user.entity.Role;
import com.threemeals.delivery.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private ReviewCommentRepository reviewCommentRepository;

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

	private static Review createMockReview(User user, Store store, Order order) {
		Review review = Review.builder()
			.store(store)
			.user(user)
			.order(order)
			.content("맛있다")
			.rating(3)
			.build();
		ReflectionTestUtils.setField(review, "createdAt", LocalDateTime.now());
		return review;
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

	@Test
	public void 리뷰_댓글_등록_성공_테스트 () {
	    // given
		Long ownerId = 1L;
		Long reviewId = 1L;

		User mockUser = createMockUser(1L);
		User mockOwner = createMockOwner(ownerId);
		Store mockStore = createMockStore(mockOwner, 1L);
		Order mockOrder = createMockOrder(mockUser, mockStore, 1L, OrderStatus.COMPLETED);
		Review mockReview = createMockReview(mockUser, mockStore, mockOrder);
		ReflectionTestUtils.setField(mockReview, "id", reviewId);

		ReviewComment mockReviewComment =
			ReviewComment.builder()
				.owner(mockOwner)
				.review(mockReview)
				.content("감사합니다.")
				.build();
		ReflectionTestUtils.setField(mockReviewComment, "createdAt", LocalDateTime.now());

		ReviewCommentRequestDto requestDto = new ReviewCommentRequestDto(reviewId, "감사합니다");

		when(reviewRepository.findReviewById(requestDto.reviewId())).thenReturn(mockReview);
		when(reviewCommentRepository.save(any(ReviewComment.class))).thenReturn(mockReviewComment);

		// when
		ReviewCommentResponseDto responseDto = reviewService.saveReviewComment(requestDto, ownerId);

		// then
		assertNotNull(responseDto);
		assertEquals(mockReviewComment.getContent(), responseDto.content());
	}

	@Test
	public void 댓글_달려는_리뷰가_본인_가게가_아닐때_예외발생 () {
		// given
		Long ownerId = 1L;
		Long reviewId = 1L;

		User mockUser = createMockUser(1L);

		// 다른 owner의 가게
		User mockOwner = createMockOwner(2L);
		Store mockStore = createMockStore(mockOwner, 1L);

		Order mockOrder = createMockOrder(mockUser, mockStore, 1L, OrderStatus.COMPLETED);

		Review mockReview = createMockReview(mockUser, mockStore, mockOrder);
		ReflectionTestUtils.setField(mockReview, "id", reviewId);

		ReviewCommentRequestDto requestDto = new ReviewCommentRequestDto(reviewId, "감사합니다");

		given(reviewRepository.findReviewById(anyLong())).willReturn(mockReview);

		// when
		StoreAccessException exception = assertThrows(StoreAccessException.class, () ->
			reviewService.saveReviewComment(requestDto, ownerId));

		// then
		assertEquals(ErrorCode.STORE_ACCESS_DENIED, exception.getErrorCode());
	}

	@Test
	public void 리뷰_삭제_성공_테스트 () {
		// given
		Long userId = 1L;
		Long reviewId = 1L;

		User mockUser = createMockUser(userId);
		User mockOwner = createMockOwner(1L);
		Store mockStore = createMockStore(mockOwner, 1L);
		Order mockOrder = createMockOrder(mockUser, mockStore, 1L, OrderStatus.COMPLETED);
		Review mockReview = createMockReview(mockUser, mockStore, mockOrder);
		ReflectionTestUtils.setField(mockReview, "id", reviewId);

		given(reviewRepository.findReviewById(anyLong())).willReturn(mockReview);

		// when
		reviewService.deleteReview(userId, reviewId);

		// then
		assertTrue(mockReview.getIsDeleted());

		verify(reviewRepository, times(1)).findReviewById(reviewId);
	}
	
	@Test
	public void 리뷰_삭제_실패_태스트 () {
	    // given
	    Long userId = 1L;
		Long reviewId = 1L;

		User mockUser = createMockUser(2L);
		User mockOwner = createMockOwner(1L);
		Store mockStore = createMockStore(mockOwner, 1L);
		Order mockOrder = createMockOrder(mockUser, mockStore, 1L, OrderStatus.COMPLETED);
		Review mockReview = createMockReview(mockUser, mockStore, mockOrder);
		ReflectionTestUtils.setField(mockReview, "id", reviewId);

		given(reviewRepository.findReviewById(reviewId)).willReturn(mockReview);

		// when
		AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> reviewService.deleteReview(userId, reviewId));

	    // then
		assertEquals(ErrorCode.REVIEW_ACCESS_DENIED, exception.getErrorCode());

		verify(reviewRepository, times(1)).findReviewById(reviewId);
	}

	@Test
	public void 댓글_삭제_성공_테스트 () {
	    // given
		Long ownerId = 1L;
		Long reviewId = 1L;

		User mockUser = createMockUser(1L);
		User mockOwner = createMockOwner(ownerId);
		Store mockStore = createMockStore(mockOwner, 1L);
		Order mockOrder = createMockOrder(mockUser, mockStore, 1L, OrderStatus.COMPLETED);
		Review mockReview = createMockReview(mockUser, mockStore, mockOrder);
		ReflectionTestUtils.setField(mockReview, "id", reviewId);

		ReviewComment mockReviewComment =
			ReviewComment.builder()
				.owner(mockOwner)
				.review(mockReview)
				.content("감사합니다.")
				.build();
		ReflectionTestUtils.setField(mockReviewComment, "createdAt", LocalDateTime.now());

		given(reviewCommentRepository.findReviewComment(anyLong())).willReturn(mockReviewComment);

	    // when
		reviewService.deleteReviewComment(ownerId, reviewId);

	    // then
		assertTrue(mockReviewComment.getIsDeleted());

		verify(reviewCommentRepository, times(1)).findReviewComment(reviewId);
	}

	
}
