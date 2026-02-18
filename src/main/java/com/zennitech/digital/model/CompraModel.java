package com.zennitech.digital.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompraModel {

    private Long id;

    private String tipoCompra;
    private LocalDate fechaIngreso;
    private String tipoDocumento;
    private String numeroDocumento;

    private String observacion;
    private List<CompraDetalleModel> materiales;
    private String estado;

    // Auditoría
    private String usuarioCreacion;
    private LocalDateTime fechaCreacion;
    private String actualizadoPor;
    private LocalDateTime actualizadoEn;

}
