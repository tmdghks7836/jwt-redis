package com.jwt.redis.model.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCreationRequest {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

}
