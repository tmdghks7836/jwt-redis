package com.jwt.radis.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "CP9995", "요청한 리소스를 찾을 수 없습니다."),
    PAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "CP9996", "요청한 페이지를 찾을 수 없습니다."),
    NOT_MATCHED_PASSWORD(HttpStatus.NOT_FOUND, "CP9997", "패스워드가 맞지 않습니다."),
    ALREADY_EXISTS_ENTITY(HttpStatus.BAD_REQUEST, "CP9998", "이미 저장된 데이터에 중복된 생성 요청을 했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String description;

}
