package com.threemeals.delivery.domain.menu.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.threemeals.delivery.domain.common.entity.BaseEntity;
import com.threemeals.delivery.domain.menu.dto.request.MenuRequestDto;
import com.threemeals.delivery.domain.menu.exception.DeletedMenuException;
import com.threemeals.delivery.domain.store.entity.Store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "menu")
public class Menu extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@Column(name = "category", nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Category category;

	@Column(name = "menu_name", nullable = false)
	private String menuName;

	@Column(name = "description")
	private String description;

	@Column(name = "price", nullable = false)
	private Integer price;

	@Column(name = "menu_img_url")
	private String menuImgUrl;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted;

	@Builder
	public Menu(Category category, String menuName, String description, Integer price, String menuImgUrl) {
		this.category = category;
		this.menuName = menuName;
		this.description = description;
		this.price = price;
		this.menuImgUrl = menuImgUrl;
		isDeleted = false;
	}

	public void updateMe(MenuRequestDto requestDto) {
		this.category = Category.of(requestDto.category());
		this.menuName = requestDto.menuName();
		this.description = requestDto.description();
		this.price = requestDto.price();
		this.menuImgUrl = requestDto.menuImgUrl();
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public void validateIsDeleted() {
		if (isDeleted) {
			throw new DeletedMenuException();
		}
	}

	public void deleteMe() {
		isDeleted = true;
	}



}
