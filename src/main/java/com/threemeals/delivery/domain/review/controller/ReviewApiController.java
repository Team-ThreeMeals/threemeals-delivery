package com.threemeals.delivery.domain.review.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.threemeals.delivery.domain.auth.UserPrincipal;
import com.threemeals.delivery.domain.auth.annotation.Authentication;
import com.threemeals.delivery.domain.review.dto.request.GetReviewRequestDto;
import com.threemeals.delivery.domain.review.dto.request.ReviewCommentRequestDto;
import com.threemeals.delivery.domain.review.dto.request.ReviewRequestDto;
import com.threemeals.delivery.domain.review.dto.response.GetReviewResponseDto;
import com.threemeals.delivery.domain.review.dto.response.ReviewCommentResponseDto;
import com.threemeals.delivery.domain.review.dto.response.ReviewResponseDto;
import com.threemeals.delivery.domain.review.service.ReviewService;
import com.threemeals.delivery.domain.user.annotation.StoreOwnerOnly;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewApiController {

	private final ReviewService reviewService;

	@PostMapping
	public ResponseEntity<ReviewResponseDto> saveReview(@Authentication UserPrincipal userPrincipal,
		@Valid @RequestBody ReviewRequestDto requestDto) {
		Long userId = userPrincipal.getUserId();
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(reviewService.saveReview(requestDto, userId));
	}

	@StoreOwnerOnly
	@PostMapping("/comments")
	public ResponseEntity<ReviewCommentResponseDto> saveReviewComment(@Authentication UserPrincipal userPrincipal,
		@Valid @RequestBody ReviewCommentRequestDto requestDto) {
		Long ownerId = userPrincipal.getUserId();
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(reviewService.saveReviewComment(requestDto, ownerId));
	}

	@GetMapping
	public ResponseEntity<Page<GetReviewResponseDto>> getStoreAllReviews(@RequestParam(defaultValue = "1") int page,
		@Valid @RequestBody GetReviewRequestDto requestDto) {
		Pageable pageable = PageRequest.of(page - 1, 10);
		return ResponseEntity.ok(reviewService.getStoreAllReviews(requestDto, pageable));
	}

	@DeleteMapping("/{reviewId}")
	public ResponseEntity<Void> deleteReview (@Authentication UserPrincipal userPrincipal, @PathVariable Long reviewId) {
		Long userId = userPrincipal.getUserId();
		reviewService.deleteReview(userId, reviewId);
		return ResponseEntity.ok().build();
	}

}
