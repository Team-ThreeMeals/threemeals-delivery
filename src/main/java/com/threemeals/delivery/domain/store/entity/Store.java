package com.threemeals.delivery.domain.store.entity;

import java.time.LocalTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.threemeals.delivery.domain.common.entity.BaseEntity;
import com.threemeals.delivery.domain.user.entity.User;

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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "store")
@EntityListeners(AuditingEntityListener.class)
public class Store extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private Long id;

	@JoinColumn(name = "owner_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY) // 한 명의 사장님 계정으로 최대 3개의 가게까지 오픈 가능
	private User owner;

	@Column(name = "store_name", nullable = false)
	private String storeName;

	@Column(name = "store_profile_img_url")
	private String storeProfileImgUrl;

	@Column(name = "address", nullable = false)
	private String address;

	@Column(name = "opening_time", nullable = false)
	private LocalTime openingTime;

	@Column(name = "closing_time", nullable = false)
	private LocalTime  closingTime;

	@Column(name = "is_closed")
	private Boolean isClosed;

	@Column(name = "delivery_tip", nullable = false)
	private Integer deliveryTip;

}
