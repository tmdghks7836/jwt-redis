package com.jwt.szs.model.dto;

import com.jwt.szs.model.base.RegisteredUser;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor()
@AllArgsConstructor
public class MemberResponse implements RegisteredUser {

    private Long id;

    private String username;

}
