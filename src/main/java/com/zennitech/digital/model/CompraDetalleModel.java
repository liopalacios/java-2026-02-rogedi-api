package com.zennitech.digital.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompraDetalleModel {
    private Long id;
    private Long idCompra;

    private Long materialId;
    private String codigoMaterial;
    private String descripcionMaterial;
    private String codigo;
    private String descripcion;
    private int cantidad;
    private List<String> series;
    private Long compraDetalleId;
    private double precio;
    private double precioTotal;
    // Auditoría
    private String usuarioCreacion;
    private LocalDateTime fechaCreacion;
}
