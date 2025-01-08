package com.threemeals.delivery.domain.review.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.threemeals.delivery.domain.auth.UserPrincipal;
import com.threemeals.delivery.domain.auth.annotation.Authentication;
import com.threemeals.delivery.domain.review.dto.request.ReviewRequestDto;
import com.threemeals.delivery.domain.review.dto.response.ReviewResponseDto;
import com.threemeals.delivery.domain.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewApiController {

	private final ReviewService reviewService;

	@PostMapping
	public ResponseEntity<ReviewResponseDto> saveReview(@Authentication UserPrincipal userPrincipal,
		@RequestBody ReviewRequestDto requestDto) {
		Long userId = userPrincipal.getUserId();
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(reviewService.saveReview(requestDto, userId));
	}
}
