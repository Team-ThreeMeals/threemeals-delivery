package com.threemeals.delivery.config.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Basic
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "GENERAL_400", "올바르지 않은 요청입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "GENERAL_405", "잘못된 HTTP 메서드를 호출했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GENERAL_500", "서버에 에러가 발생했습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "GENERAL_404", "존재하지 않는 엔티티입니다."),
    ENTITY_ALREADY_EXISTS(HttpStatus.CONFLICT, "GENERAL_409", "이미 존재하는 엔티티입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "GENERAL_403", "접근 권한이 없습니다."),

    // Role
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "ROLE_400", "유효하지 않은 권한입니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404", "존재하지 않는 유저입니다."),
    USER_DELETED(HttpStatus.FORBIDDEN, "USER_403", "삭제된 유저입니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_409", "이미 존재하는 유저입니다."),

    // Authentication
    AUTHENTICATION_FAILURE(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_401", "인증에 실패했습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_401", "아이디 혹은 비밀번호가 올바르지 않습니다."),

    // Token
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN_400", "유효하지 않은 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "TOKEN_404", "존재하지 않는 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_401", "만료된 토큰입니다."),

    // Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_404", "존재하지 않는 댓글입니다."),
    COMMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "COMMENT_403", "해당 댓글에 대한 접근 권한이 없습니다."),

    // Review
    REVIEW_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "REVIEW_400", "배달 진행 중으로 리뷰를 남길 수 없습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_404", "존재하지 않는 리뷰입니다."),
    REVIEW_ACCESS_DENIED(HttpStatus.FORBIDDEN,"REVIEW_403", "해당 리뷰에 대한 접근 권한이 없습니다."),
    REVIEW_RATING_RANGE_BAD(HttpStatus.BAD_REQUEST, "REVIEW_400", "범위가 유효하지 않습니다."),

    // Menu
    INVALID_MENU_CATEGORY(HttpStatus.BAD_REQUEST, "MENU_400", "유효하지 않은 메뉴 카테고리입니다."),
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "MENU_404", "존재하지 않는 메뉴입니다."),
    MENU_DELETED(HttpStatus.FORBIDDEN, "MENU_403", "삭제된 메뉴입니다."),

    // MenuOption
    MENU_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "MENU_OPTION_404", "존재하지 않는 메뉴 옵션입니다."),
    MENU_OPTION_DELETED(HttpStatus.FORBIDDEN, "MENU_OPTION_403", "삭제된 메뉴 옵션입니다."),

    // Store
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_404", "존재하지 않는 가게입니다."),
    STORE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST,"STORE_400","사장님은 가게를 최대 3개까지 소유할 수 있습니다."),
    STORE_DELETED(HttpStatus.FORBIDDEN,"STORE_403","접근권한이 없습니다."),

    // Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_404", "존재하지 않는 주문입니다."),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(final HttpStatus status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}