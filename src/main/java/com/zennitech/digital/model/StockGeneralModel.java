package com.zennitech.digital.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockGeneralModel {
    private Long materialId;
    private String sap;
    private String articulo;
    private String unidadMedida;
    private String contratista;
    private Long contratistaId;
    private String condicion;
    private String zonal;

    // Cantidades por estado
    private Integer stockDisponible;
    private Integer stockEnUso;
    private Integer stockMantenimiento;
    private Integer tieneSeries;
    private Integer stockTotal;

    // Marca visual
    private String marcaSeries; // "S" o ""
    private Integer enTransito;
}
