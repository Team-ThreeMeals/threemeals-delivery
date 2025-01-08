package com.threemeals.delivery.domain.user.repository;

import java.util.List;

import com.threemeals.delivery.domain.user.entity.User;

public interface UserRepositoryCustom {

	List<User> findAllCustom();
}
