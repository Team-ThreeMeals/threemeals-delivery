package com.threemeals.delivery.domain.menu.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.threemeals.delivery.domain.common.entity.BaseEntity;
import com.threemeals.delivery.domain.menu.dto.request.MenuOptionRequestDto;
import com.threemeals.delivery.domain.menu.dto.request.MenuRequestDto;
import com.threemeals.delivery.domain.menu.exception.DeletedMenuOptionException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@Table(name = "menu_option")
public class MenuOption extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_id", nullable = false)
	private Menu menu;

	@Column(name = "menu_option_name", nullable = false)
	private String menuOptionName;

	@Column(name = "description")
	private String description;

	@Column(name = "menu_option_price", nullable = false)
	private Integer menuOptionPrice;

	@Column(name = "menu_option_img_url")
	private String menuOptionImgUrl;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted;

	@Builder
	public MenuOption(String menuOptionName, String description, Integer menuOptionPrice, String menuOptionImgUrl) {
		this.menuOptionName = menuOptionName;
		this.description = description;
		this.menuOptionPrice = menuOptionPrice;
		this.menuOptionImgUrl = menuOptionImgUrl;
		isDeleted = false;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public void updateMe(MenuOptionRequestDto requestDto) {
		this.menuOptionName = requestDto.menuOptionName();
		this.description = requestDto.description();
		this.menuOptionPrice = requestDto.menuOptionPrice();
		this.menuOptionImgUrl = requestDto.menuOptionImgUrl();
	}

	public void validateIsDeleted() {
		if (isDeleted) {
			throw new DeletedMenuOptionException();
		}
	}

	public void deleteMe() {
		isDeleted = true;
	}

}
