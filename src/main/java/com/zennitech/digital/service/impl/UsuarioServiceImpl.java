package com.zennitech.digital.service.impl;

import com.zennitech.digital.config.JwtUtil;
import com.zennitech.digital.model.UsuarioModel;
import com.zennitech.digital.pojo.AuthResponse;
import com.zennitech.digital.repository.UsuarioJdbcRepository;
import com.zennitech.digital.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioJdbcRepository usuarioJdbcRepository;


    private final JwtUtil jwtUtil;


    private final PasswordEncoder passwordEncoder;


    @Override
    public AuthResponse login(String username, String password) {
        Optional<UsuarioModel> userOpt = usuarioJdbcRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        UsuarioModel user = userOpt.get();
        System.out.println(password+" "+user.getPassword()  );
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRoles());

        return AuthResponse.builder()
                .jwt(token)
                .rol(user.getRoles())
                .accesos(
                        Optional.ofNullable(user.getAccesos())
                                .orElse(Collections.emptyList())
                                .stream()
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList())
                )
                .build();
    }
    // Implementación del método para actualizar los datos del usuario

}
