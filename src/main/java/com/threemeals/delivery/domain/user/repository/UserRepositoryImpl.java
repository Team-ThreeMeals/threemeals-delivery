package com.threemeals.delivery.domain.user.repository;

import static com.threemeals.delivery.domain.user.entity.QUser.*;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.threemeals.delivery.domain.user.entity.User;

import jakarta.persistence.EntityManager;

public class UserRepositoryImpl implements UserRepositoryCustom{

	private final JPAQueryFactory queryFactory;

	public UserRepositoryImpl(EntityManager entityManager) {
		this.queryFactory = new JPAQueryFactory(entityManager);
	}

	@Override
	public List<User> findAllCustom() {
		return queryFactory
			.select(user)
			.from(user)
			.fetch();
	}

}
