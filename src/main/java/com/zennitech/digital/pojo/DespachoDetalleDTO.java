package com.zennitech.digital.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DespachoDetalleDTO {
    private Integer numero;
    private String codigoSap;
    private String descripcion;
    private String series;  // Lista de series separadas por comas
    private String unidadMedida;
    private Integer cantidad;
}
