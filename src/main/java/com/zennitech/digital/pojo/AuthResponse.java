package com.zennitech.digital.pojo;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class AuthResponse {
    private String jwt;
    private List<String> rol;
    private List<String> accesos;
}
