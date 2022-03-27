package com.jwt.szs.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.szs.exception.ErrorCode;
import com.jwt.szs.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Deprecated
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

   private ObjectMapper objectMapper = new ObjectMapper();

   @Override
   public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
      //필요한 권한이 없이 접근하려 할때 403
      response.setStatus(HttpStatus.FORBIDDEN.value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.getOutputStream()
              .write(objectMapper.writeValueAsString(
                              ErrorResponse.getByErrorCode(ErrorCode.NOT_FOUND_PERMISSION)
                      ).getBytes(StandardCharsets.UTF_8)
              );
   }
}
