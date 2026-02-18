package com.zennitech.digital.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class LiquidacionModel {
    private Long id;
    private String coLiquidacion;
    private Integer numeroSecuencia;
    private LocalDateTime fechaInstalacion;
    private String tipoPartida;

    // Para relaciones
    private String tipoPropiedadCodigo;
    private String tipoPropiedadNombre;
    private String tipoInstalacionCodigo;
    private String tipoInstalacionNombre;
    private String estadoRevisionCodigo;
    private String estadoRevisionNombre;

    private String numeroActa;
    private String codigoPedido;
    private String dniCliente;
    private String nombreCliente;
    private String direccion;
    private String torreDepartamento;
    private String nombreCondominio;
    private String paqueteServicio;
    private BigDecimal metraje;
    private Integer cantidadMesh;
    private String rotuladoCtoNap;
    private String observacionContrata;
    private String observacionOperador;

    private String zonal;
    private String paqueteCodigo;

    // Campos de auditoría
    private LocalDateTime creadoEn;
    private String creadoPor;
    private String estado;

    // Métodos helper
    public String getTipoInstalacionDisplay() {
        return tipoInstalacionNombre != null ? tipoInstalacionNombre : tipoInstalacionCodigo;
    }

    public String getEstadoRevisionDisplay() {
        return estadoRevisionNombre != null ? estadoRevisionNombre : estadoRevisionCodigo;
    }

    public String getTipoPropiedadDisplay() {
        return tipoPropiedadNombre != null ? tipoPropiedadNombre : tipoPropiedadCodigo;
    }
}
