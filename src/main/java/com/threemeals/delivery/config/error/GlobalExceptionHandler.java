package com.threemeals.delivery.config.error;

import com.threemeals.delivery.domain.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 이 예외는 validation 전용 예외라서 비즈니스 예외 처리 클래스에 있는 게 조금 안 어울리기는 함. 크기가 커지면 따로 빼서 관리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException", e);

        String errorMessages = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining("\n"));

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_REQUEST, errorMessages);

        return ResponseEntity
                .badRequest()
                .body(errorResponse);
    }


    // 잘못된 HttpMethod 요청 왔을 때 (컨트롤러에서 정의되지 않은 Http 메서드 요청할 때)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException", e);
        return createErrorResponseEntity(ErrorCode.METHOD_NOT_ALLOWED);
    }

    // Business Exception(대부분 여기서 걸림)
    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessBaseException(BaseException e) {
        log.error("BusinessBaseException", e);
        return createErrorResponseEntity(e.getErrorCode());
    }

    // 500 서버 에러 정도만 여기서 걸림
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        e.printStackTrace();
        log.error("Exception", e);
        return createErrorResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    // make ErrorResponse
    private ResponseEntity<ErrorResponse> createErrorResponseEntity(ErrorCode errorCode) {
        return new ResponseEntity<>(ErrorResponse.of(errorCode), errorCode.getStatus());
    }
}
