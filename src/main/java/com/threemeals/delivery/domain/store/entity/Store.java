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
import lombok.Builder;
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
	private LocalTime closingTime;

	@Column(name = "is_closed")
	private Boolean isClosed;

	@Column(name = "delivery_tip", nullable = false)
	private Integer deliveryTip;

	@Column(name = "minimum_order_price", nullable = false)
	private Integer minOrderPrice;

	@Builder
	public Store(User owner, String storeName, String storeProfileImgUrl, String address,
		LocalTime openingTime, LocalTime closingTime, Integer deliveryTip, Integer minOrderPrice) {
		this.owner = owner;
		this.storeName = storeName;
		this.storeProfileImgUrl = storeProfileImgUrl;
		this.address = address;
		this.openingTime = openingTime;
		this.closingTime = closingTime;
		this.deliveryTip = deliveryTip;
		this.minOrderPrice = minOrderPrice;
		isClosed = false;
	}

	public void update(String storeName, String storeProfileImgUrl, String address,
		LocalTime openingTime, LocalTime closingTime, Integer deliveryTip, Integer minOrderPrice) {
		if (storeName != null)
			this.storeName = storeName;
		if (storeProfileImgUrl != null)
			this.storeProfileImgUrl = storeProfileImgUrl;
		if (address != null)
			this.address = address;
		if (openingTime != null)
			this.openingTime = openingTime;
		if (closingTime != null)
			this.closingTime = closingTime;
		if (deliveryTip != null)
			this.deliveryTip = deliveryTip;
		if (minOrderPrice != null)
			this.minOrderPrice = minOrderPrice;
	}

	public void storeClosed() {
		this.isClosed = true;
	}

}
