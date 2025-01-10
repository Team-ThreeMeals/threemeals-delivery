package com.threemeals.delivery.domain.menu.repository;

import static com.threemeals.delivery.domain.menu.entity.QMenu.*;
import static com.threemeals.delivery.domain.store.entity.QStore.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.threemeals.delivery.domain.menu.dto.response.MenuResponseDto;

import jakarta.persistence.EntityManager;

public class MenuRepositoryImpl implements MenuRepositoryForQueryDSL {

	private final JPAQueryFactory queryFactory;

	public MenuRepositoryImpl(EntityManager entityManager) {
		this.queryFactory = new JPAQueryFactory(entityManager);
	}

	// Store 도메인에 해당 로직이 없고, 내가 직접 만들기에는 충돌 우려가 나서, 임시로 만듦
	@Override
	public boolean existsByMenuIdAndOwnerId(Long menuId, Long ownerId) {
		return queryFactory
			.selectOne()
			.from(menu)
			.join(menu.store, store)
			.where(
				menu.id.eq(menuId),
				store.owner.id.eq(ownerId)
			)
			.fetchFirst() != null;
	}

	// MenuResponse 필드 바뀌면 예외 발생해서 그리 좋은 코드는 아님. Repository에서 try-catch를 사용해야 할까?
	@Override
	public Page<MenuResponseDto> findAllMenuByStoreId(Long storeId, Pageable pageable) {
		List<MenuResponseDto> content = queryFactory.select(Projections.constructor(MenuResponseDto.class,
				menu.id,
				menu.menuName,
				menu.description,
				menu.price,
				menu.menuImgUrl
			))
			.from(menu)
			.join(menu.store, store)
			.where(store.id.eq(storeId), menu.isDeleted.isFalse())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(menu.updatedAt.desc())
			.fetch();

		JPAQuery<Long> queryCount = queryFactory
			.select(menu.count())
			.from(menu)
			.join(menu.store, store)
			.where(store.id.eq(storeId), menu.isDeleted.isFalse());

		return PageableExecutionUtils.getPage(content, pageable, queryCount::fetchOne);
	}

}
