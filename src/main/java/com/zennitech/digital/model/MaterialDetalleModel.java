package com.zennitech.digital.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialDetalleModel {
    private Long id;
    private Long materialId;
    private String numeroSerie;
    private String modelo;
    private Integer anioFabricacion;
    private String estado;
    private LocalDate fechaCompra;
    private LocalDate fechaGarantia;
    private String ubicacion;
    private String creadoPor;
    private LocalDateTime creadoEn;
    private String actualizadoPor;
    private LocalDateTime actualizadoEn;
}
