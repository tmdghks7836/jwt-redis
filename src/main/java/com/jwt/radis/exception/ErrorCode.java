package com.jwt.radis.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    DID_NOT_EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "CP9992", "아직 만료되지 않은 토큰입니다다."),
    NOT_MATCHED_VALUE(HttpStatus.BAD_REQUEST, "CP9993", "저장된 값과 일지하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.NOT_FOUND, "CP9994", "이미 만료된 리프레시토큰입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "CP9995", "요청한 리소스를 찾을 수 없습니다."),
    PAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "CP9996", "요청한 페이지를 찾을 수 없습니다."),
    NOT_MATCHED_PASSWORD(HttpStatus.NOT_FOUND, "CP9997", "패스워드가 맞지 않습니다."),
    ALREADY_EXISTS_ENTITY(HttpStatus.BAD_REQUEST, "CP9998", "이미 저장된 데이터에 중복된 생성 요청을 했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String description;

}
