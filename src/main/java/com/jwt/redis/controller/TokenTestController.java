package com.jwt.redis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test/token")
public class TokenTestController {

    @GetMapping
    public ResponseEntity test(){

        return ResponseEntity.ok().build();
    }
}
