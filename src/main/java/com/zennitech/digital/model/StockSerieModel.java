package com.zennitech.digital.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockSerieModel {
    private Long id;
    private String contratista;
    private String condicion;
    private Integer idZonal;
    private String zonal; // Para mostrar el nombre
    private String sap;
    private String articulo;
    private String unidadMedida;
    private String serie;
    private String estado;
    private String ultimoMovimiento;
    private Integer dias;

    // Datos adicionales
    private String modelo;
    private LocalDate fechaCompra;
    private String ubicacion;
}
