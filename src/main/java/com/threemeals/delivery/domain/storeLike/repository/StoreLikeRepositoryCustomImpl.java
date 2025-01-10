package com.threemeals.delivery.domain.storeLike.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.threemeals.delivery.domain.storeLike.entity.QStoreLike;
import com.threemeals.delivery.domain.storeLike.entity.StoreLike;

import jakarta.persistence.EntityManager;

public class StoreLikeRepositoryCustomImpl implements StoreLikeRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public StoreLikeRepositoryCustomImpl(EntityManager entityManager) {
		this.queryFactory = new JPAQueryFactory(entityManager);

	}

	@Override
	public StoreLike findByUserIdAndStoreId(Long userId, Long storeId) {
		QStoreLike storeLike = QStoreLike.storeLike;

		return queryFactory.selectFrom(storeLike)
			.where(storeLike.user.id.eq(userId),
				storeLike.store.id.eq(storeId))
			.fetchOne();
	}

	@Override
	public Page<StoreLike> findAllByUserIdAndIsActiveTrue(Long userId, Pageable pageable) {
		QStoreLike storeLike = QStoreLike.storeLike;

		List<StoreLike> results = queryFactory.selectFrom(storeLike)
			.where(storeLike.user.id.eq(userId), storeLike.isActive.isTrue())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(storeLike.count())
			.from(storeLike)
			.where(storeLike.user.id.eq(userId).and(storeLike.isActive.isTrue()));

		return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
	}
}

