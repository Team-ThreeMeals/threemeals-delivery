package com.threemeals.delivery.domain.review.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.threemeals.delivery.domain.common.entity.BaseEntity;
import com.threemeals.delivery.domain.order.entity.Order;
import com.threemeals.delivery.domain.store.entity.Store;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "review")
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY) // ManyToOne 맞나. 헷갈리네...
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	// User도 반드시 알 필요가 있을까? Order가 알고 있는 거 같아서...
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// store 알 필요가 있을까? 이미 Order가 알고 있음. 참조가 좀 복잡해지기는 하네...
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@Column(name = "rating", nullable = false)
	private Integer rating;

	@Column(name = "content", length = 500)
	private String content;

	@Column(name = "review_image_url")
	private String reviewImageUrl;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted;

	@Builder
	public Review(Order order, User user, Store store,  Integer rating, String content, String reviewImageUrl) {
		this.order = order;
		this.user = user;
		this.store = store;
		this.rating = rating;
		this.content = content;
		this.reviewImageUrl = reviewImageUrl;
		this.isDeleted = false;
	}

}
