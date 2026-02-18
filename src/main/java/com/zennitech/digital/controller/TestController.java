package com.zennitech.digital.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    private final PasswordEncoder encoder;

    public TestController(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @GetMapping("/bcrypt")
    public String encode(@RequestParam String pwd) {
        System.out.printf("TEST-01");
        System.out.println("TEST-02");
        System.out.println("Encoded in password: " + pwd + " -> " + encoder.encode(pwd) + "\n" );
        return encoder.encode(pwd);
    }
}
