package com.zennitech.digital.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContratistaModel {
    private Integer id;
    private String razonSocial;
    private String ruc;
    private String codigo;
    private String tipo;
    private String contacto;
    private String telefono;
    private String email;
    private String direccion;
    private Boolean activo;
    private String creadoPor;
    private LocalDateTime creadoEn;
}
