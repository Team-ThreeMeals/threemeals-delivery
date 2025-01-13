package com.threemeals.delivery.domain.store.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.threemeals.delivery.domain.store.entity.QStore;
import com.threemeals.delivery.domain.store.entity.Store;

import jakarta.persistence.EntityManager;

public class StoreRepositoryCustomImpl implements StoreRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public StoreRepositoryCustomImpl(EntityManager entityManager) {
		this.queryFactory = new JPAQueryFactory(entityManager);
	}

	@Override
	public long countByOwnerIdAndIsClosedFalse(Long ownerId) {
		QStore store = QStore.store;

		return queryFactory
			.select(store.count())
			.from(store)
			.where(store.owner.id.eq(ownerId)
				.and(store.isClosed.eq(false)))
			.fetchOne();
	}

	@Override
	public Page<Store> findByStoreNameContainingAndIsClosedFalse(String storeName, Pageable pageable) {
		QStore store = QStore.store;

		List<Store> content = queryFactory
			.selectFrom(store)
			.where(store.storeName.contains(storeName)
				.and(store.isClosed.eq(false)))
			.orderBy(store.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(store.count())
			.from(store)
			.where(store.storeName.contains(storeName)
				.and(store.isClosed.eq(false)));

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	@Override
	public Page<Store> findByIsClosedFalse(Pageable pageable) {
		QStore store = QStore.store;

		// 데이터 조회
		List<Store> content = queryFactory
			.selectFrom(store)
			.where(store.isClosed.eq(false))
			.orderBy(store.updatedAt.desc()) // 정렬 기준 (예: 업데이트 시간 내림차순)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 전체 카운트 쿼리
		JPAQuery<Long> countQuery = queryFactory
			.select(store.count())
			.from(store)
			.where(store.isClosed.eq(false));

		// Page 객체 반환
		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}
}
