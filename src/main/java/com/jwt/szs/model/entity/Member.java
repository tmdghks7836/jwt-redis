package com.jwt.szs.model.entity;

import com.jwt.szs.model.base.RegisteredUser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member implements RegisteredUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column( length = 50)
    private Long id;

    @Column(name = "username", length = 50, unique = true)
    private String username;

    @Column(name = "password", length = 200)
    private String password;

    public Member(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
