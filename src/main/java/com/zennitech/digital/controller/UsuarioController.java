package com.zennitech.digital.controller;

import com.zennitech.digital.pojo.AuthRequest;
import com.zennitech.digital.pojo.AuthResponse;
import com.zennitech.digital.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UsuarioController {
    private final PasswordEncoder encoder;
    @Autowired
    private UsuarioService authService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        System.out.println("Login request: " + request.getUsername() + " - " + encoder.encode(request.getPassword()) + "..."  );
        return authService.login(request.getUsername(), request.getPassword());
    }
    @GetMapping("/pingpong")
    public String ping() {
        return "pong - " + System.currentTimeMillis();
    }
    @GetMapping("/fechahoy")
    public String getFecha() {
        return "fecha - " + System.currentTimeMillis();
    }
}
