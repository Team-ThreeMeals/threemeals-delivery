package com.threemeals.delivery.domain.menu.entity;

import static com.threemeals.delivery.config.error.ErrorCode.*;

import java.util.Arrays;

import com.threemeals.delivery.domain.common.exception.InvalidRequestException;

import lombok.Getter;

@Getter
public enum Category {
	KOREAN("한식"),        // 한식
	JAPANESE("일식"),      // 일식
	CHINESE("중식"),       // 중식
	CHICKEN("치킨"),       // 치킨
	PIZZA("피자"),         // 피자
	BURGER("버거"),        // 버거
	DESSERT("디저트"),      // 디저트
	BEVERAGE("음료"),       // 음료
	FAST_FOOD("패스트푸드"), // 패스트푸드
	SANDWICH("샌드위치"),    // 샌드위치
	STEAK("스테이크"),       // 스테이크
	SEAFOOD("해산물"),       // 해산물
	NOODLES("면 요리"),      // 면 요리
	BARBECUE("바비큐"),       // 바비큐
	VEGETARIAN("채식"),      // 채식
	INDIAN("인도 요리"),      // 인도 요리
	MEXICAN("멕시코 요리"),   // 멕시코 요리
	THAI("태국 요리"),        // 태국 요리
	VIETNAMESE("베트남 요리"), // 베트남 요리
	SNACKS("간식");          // 간식

	private final String koreanName;

	Category(String koreanName) {
		this.koreanName = koreanName;
	}

	public static Category of(String category) {
		return Arrays.stream(Category.values())
			.filter(cat -> cat.name().equalsIgnoreCase(category))
			.findFirst()
			.orElseThrow(() -> new InvalidRequestException(INVALID_MENU_CATEGORY));
	}
}