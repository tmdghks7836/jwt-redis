package com.jwt.radis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/test/token")
public class TokenTestController {

    @GetMapping
    public ResponseEntity test(){

        return ResponseEntity.ok().build();
    }
}
