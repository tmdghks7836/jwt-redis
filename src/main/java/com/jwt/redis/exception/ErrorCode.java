package com.jwt.redis.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    NOT_YET_EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "E0001", "아직 만료되지 않은 토큰입니다다."),
    NOT_MATCHED_VALUE(HttpStatus.BAD_REQUEST, "E0002", "저장된 값과 일지하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.NOT_FOUND, "E0003", "이미 만료된 리프레시토큰입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "E0004", "요청한 리소스를 찾을 수 없습니다."),
    PAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "E0005", "요청한 페이지를 찾을 수 없습니다."),
    NOT_MATCHED_PASSWORD(HttpStatus.NOT_FOUND, "E0006", "패스워드가 맞지 않습니다."),
    ALREADY_EXISTS_ENTITY(HttpStatus.BAD_REQUEST, "E0007", "이미 저장된 데이터에 중복된 생성 요청을 했습니다."),
    NOT_FOUND_PERMISSION(HttpStatus.NOT_FOUND, "E0008", "권한이 없습니다."),
    AUTHENTICATION_FAIL(HttpStatus.UNAUTHORIZED, "E0009", "인증에 실패하였습니다."),
    ALREADY_DEFINED(HttpStatus.BAD_REQUEST, "E0010", "이미 정의된 데이터입니다."),
    NOT_MATCHED_PARAM_TYPE(HttpStatus.BAD_REQUEST, "E0011", "파라미터 타입이 맞지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String description;

}
