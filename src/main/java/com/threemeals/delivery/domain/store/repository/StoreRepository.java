package com.threemeals.delivery.domain.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.threemeals.delivery.domain.store.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
