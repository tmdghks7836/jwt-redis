package com.jwt.redis.model.dto;

import com.jwt.redis.model.base.RegisteredUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor()
@AllArgsConstructor
public class MemberResponse implements RegisteredUser {

    private Long id;

    private String username;

    private String password;
}
