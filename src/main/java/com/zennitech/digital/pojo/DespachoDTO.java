package com.zennitech.digital.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DespachoDTO {
    // Encabezado
    private String empresa;
    private String ruc;
    private String tipoDocumento;
    private String numeroDocumento;
    private LocalDate fecha;
    private String titulo;

    // Datos del técnico
    private String dni;
    private String ciudad;
    private Integer numeroMovimiento;
    private String nombreTecnico;

    // Detalles del despacho
    private List<DespachoDetalleDTO> detalles;

    // Firmas
    private String tecnicoResponsable;
    private String almacen;

    // Información adicional
    private String observaciones;
    private String generadoPor;
}
