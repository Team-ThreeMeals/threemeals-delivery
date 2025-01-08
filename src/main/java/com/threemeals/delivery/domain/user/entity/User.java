package com.threemeals.delivery.domain.user.entity;

import com.threemeals.delivery.domain.common.entity.BaseEntity;
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

	@Column(name = "role", nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private Role role;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted;

	@Builder
	public User(String username, String email, String password, Role role, String address) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.role = role;
		this.address = address;
		isDeleted = false;
	}

	public void validateIsDeleted() {
		if (isDeleted) {
			throw new DeletedUserException();
		}
	}

}
