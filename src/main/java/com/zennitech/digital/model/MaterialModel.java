package com.zennitech.digital.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialModel {
    private Long id;

    private String codigo;
    private String descripcion;
    private Integer idUnidadMedida;
    private String unidadMedida; // Para mostrar
    private Integer idContratista;
    private String contratista; // Para mostrar

    private Integer cantidadSeries;
    private Boolean activo;
    private Boolean seriado;
    // Auditoría
    private String creadoPor;
    private LocalDateTime creadoEn;
    private String actualizadoPor;
    private LocalDateTime actualizadoEn;
}
