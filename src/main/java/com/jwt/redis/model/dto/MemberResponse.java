package com.jwt.redis.model.dto;

import com.jwt.redis.model.base.RegisteredUser;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor()
@AllArgsConstructor
public class MemberResponse implements RegisteredUser {

    private Long id;

    private String username;

    private String password;
}
