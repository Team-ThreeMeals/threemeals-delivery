package com.threemeals.delivery.domain.review.entity;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "review_comment") // 리뷰에 대한 사장님 답변 (테이블명이 좀 헷갈린다. 대댓글 테이블 같음)
public class ReviewComment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_id", nullable = false)
	private Review review; // 댓글이 달린 리뷰

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner; // 댓글 작성자 == 사장님. 사장님이 여러 리뷰에 답변을 달 수 있으므로 ManyToOne

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted;

	@Builder
	public ReviewComment (Review review, User owner, String content) {
		this.review = review;
		this.owner = owner;
		this.content = content;
		this.isDeleted = false;
	}
}
