package com.threemeals.delivery.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.threemeals.delivery.domain.menu.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long>, MenuRepositoryForQueryDSL {
}
