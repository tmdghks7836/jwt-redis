package com.jwt.radis.model.dto;

import com.jwt.radis.model.base.RegisteredUser;
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
