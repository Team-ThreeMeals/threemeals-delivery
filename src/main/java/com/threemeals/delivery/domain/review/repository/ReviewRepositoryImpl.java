package com.threemeals.delivery.domain.review.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.threemeals.delivery.domain.review.dto.response.GetReviewResponseDto;
import com.threemeals.delivery.domain.review.dto.response.ReviewCommentResponseDto;
import com.threemeals.delivery.domain.review.dto.response.ReviewResponseDto;
import static com.threemeals.delivery.domain.review.entity.QReview.review;
import static com.threemeals.delivery.domain.review.entity.QReviewComment.reviewComment;

import com.threemeals.delivery.domain.review.entity.Review;

import jakarta.persistence.EntityManager;

public class ReviewRepositoryImpl implements ReviewRepositoryCustom{

	private final JPAQueryFactory queryFactory;

	public ReviewRepositoryImpl(EntityManager entityManager) {
		this.queryFactory = new JPAQueryFactory(entityManager);
	}

	@Override
	public Page<GetReviewResponseDto> findAllReviewsWithComments(Long storeId, int minRating, int maxRating, Pageable pageable) {

		List<Tuple> joinComments = queryFactory
			.select(review, reviewComment)
			.from(review)
			.leftJoin(reviewComment).on(reviewComment.review.eq(review))
			.where(
				review.store.id.eq(storeId),
				review.rating.between(minRating, maxRating),
				review.isDeleted.eq(false)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Map<Review, List<ReviewCommentResponseDto>> groupedComments = joinComments.stream()
			.collect(Collectors.groupingBy(
				tuple -> tuple.get(review),
				Collectors.mapping(
					tuple -> {
						if (tuple.get(reviewComment) == null) {
							return null;
						}
						return new ReviewCommentResponseDto(
							"사장님",
							Objects.requireNonNull(tuple.get(reviewComment)).getContent(),
							Objects.requireNonNull(tuple.get(reviewComment)).getCreatedAt().toLocalDate()
						);
					},
					Collectors.filtering(Objects::nonNull, Collectors.toList()) // null 값 제외
				)
			));


		List<GetReviewResponseDto> reviews = joinComments.stream()
			.map(tuple -> new GetReviewResponseDto(
				new ReviewResponseDto(
					tuple.get(review).getStore().getStoreName(),
					tuple.get(review).getUser().getUsername(),
					tuple.get(review).getRating(),
					tuple.get(review).getContent(),
					tuple.get(review).getReviewImageUrl(),
					tuple.get(review).getCreatedAt().toLocalDate()
				),
				groupedComments.getOrDefault(tuple.get(review), Collections.emptyList())
			))
			.toList();

		JPAQuery<Long> queryCount = queryFactory
			.select(review.count())
			.from(review)
			.where(
				review.store.id.eq(storeId),
				review.rating.between(minRating, maxRating),
				review.isDeleted.eq(false)
			);

		return PageableExecutionUtils.getPage(reviews, pageable, queryCount::fetchOne);
	}
}
