package com.threemeals.delivery.domain.storeLike.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.threemeals.delivery.domain.storeLike.entity.StoreLike;

public interface StoreLikeRepository extends JpaRepository<StoreLike, Long>, StoreLikeRepositoryCustom {

}
