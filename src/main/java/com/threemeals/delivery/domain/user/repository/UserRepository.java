package com.threemeals.delivery.domain.user.repository;

import java.util.Optional;

import com.threemeals.delivery.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom{

	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);
}
