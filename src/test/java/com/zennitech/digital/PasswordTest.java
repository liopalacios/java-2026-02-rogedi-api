package com.zennitech.digital;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

        // Toma un password de tu BD y el password en texto plano
        String passwordFromDB = "$2a$10$v5lmOilqktLWQUPCXQTNx.6KOXkkcUCosdHkzSVOP.c1Jy5e3iD2y";
        String plainPassword = "87654321"; // La contraseña real

        boolean matches = encoder.matches(plainPassword, passwordFromDB);
        System.out.println("¿Coincide? " + matches);
    }
}
