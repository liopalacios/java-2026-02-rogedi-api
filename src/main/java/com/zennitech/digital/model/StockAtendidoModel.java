package com.zennitech.digital.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StockAtendidoModel {
    private Long movimientoId;
    private String codigoMovimiento;
    private String nroContratista;
    private String codZonal;
    private String tipoMovimiento;
    private String condicion;
    private String tipoDocumento;
    private Long tipoMovimientoId;
    private String numDocumento;
    private LocalDate fecMovimiento;
    private String nroDocref;
    private String nroDestinoref;
    private String glosa;
    private String usuarioRegistro;
    private LocalDateTime fechaReg;

    // Información adicional del material
    private String sap;
    private String articulo;
    private String unidadMedida;
    private String serie;
    private String contratista;
    private String zonalNombre;

    // Información del técnico
    private String tecnicoNombre;
    private String tecnicoApellido;
    private String tecnicoCodigo;

    // Datos calculados
    private Integer cantidad;
    private Integer diasAtendido;
    private String estadoMaterial;
    private Integer usuarioId;
    private LocalDateTime creadoEn;

    private String observacion;

    // Campo calculado para mostrar nombre completo del técnico
    public String getTecnicoNombreCompleto() {
        if (tecnicoNombre != null && tecnicoApellido != null) {
            return tecnicoNombre + " " + tecnicoApellido;
        } else if (tecnicoNombre != null) {
            return tecnicoNombre;
        } else if (tecnicoApellido != null) {
            return tecnicoApellido;
        }
        return "";
    }
}
