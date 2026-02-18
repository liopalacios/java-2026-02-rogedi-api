package com.zennitech.digital.service;

import com.zennitech.digital.pojo.AuthResponse;

public interface UsuarioService {
    AuthResponse login(String username, String password);
}
