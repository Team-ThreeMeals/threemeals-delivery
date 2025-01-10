package com.threemeals.delivery.domain.storeLike.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
			.where(storeLike.userId.id.eq(userId), // userId의 id 필드 비교
				storeLike.storeId.id.eq(storeId)) // storeId의 id 필드 비교
			.fetchOne();
	}

	@Override
	public Page<StoreLike> findAllByUserIdAndIsActiveTrue(Long userId, Pageable pageable) {
		QStoreLike storeLike = QStoreLike.storeLike;

		List<StoreLike> results = queryFactory.selectFrom(storeLike)
			.where(storeLike.userId.id.eq(userId), storeLike.isActive.isTrue())
			.offset(pageable.getOffset()) // 시작 지점
			.limit(pageable.getPageSize()) // 페이지 크기
			.fetch();

		// 전체 데이터 수 조회
		long total = queryFactory.selectFrom(storeLike)
			.where(storeLike.userId.id.eq(userId), storeLike.isActive.isTrue())
			.fetchCount();

		// Page 객체 반환
		return new PageImpl<>(results, pageable, total);
	}
}

