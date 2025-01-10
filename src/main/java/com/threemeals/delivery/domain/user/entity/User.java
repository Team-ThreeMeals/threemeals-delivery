package com.threemeals.delivery.domain.user.entity;

import com.threemeals.delivery.domain.common.entity.BaseEntity;
import com.threemeals.delivery.domain.common.exception.AccessDeniedException;
import com.threemeals.delivery.domain.user.dto.request.UpdateUserRequestDto;
import com.threemeals.delivery.domain.user.exception.DeletedUserException;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user")
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private Long id;

	@Column(name = "username", nullable = false)
	private String username;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "address", nullable = false)
	private String address;

	@Column(name = "profile_img_url")
	private String profileImgUrl;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted;

	@Column(name = "role", nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	@Builder
	public User(String username, String email, String password, Role role, String address, String profileImgUrl) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.role = role;
		this.address = address;
		this.profileImgUrl = profileImgUrl;
		isDeleted = false;
	}

	public void update(String username) {
		validateIsDeleted();  // 삭제된 유저인지 먼저 체크
		this.username = username;
	}

	public void validateIsDeleted() {
		if (isDeleted) {
			throw new DeletedUserException();
		}
	}

	public void updateMe(UpdateUserRequestDto requestDto, String encodedPassword) {
		this.username = requestDto.username();
		this.password = encodedPassword;
		this.address = requestDto.address();
		this.profileImgUrl = requestDto.profileImgUrl();
	}

	public void deleteMe() {
		isDeleted = true;
	}

	public void validateIsOwner() {
		validateIsDeleted();
		if (role != Role.STORE_OWNER) {
			throw new AccessDeniedException();
		}
	}

}
