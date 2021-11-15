package com.jwt.radis.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor()
@AllArgsConstructor
public class MemberResponse {

    private Long id;

    private String username;

    private String password;
}
