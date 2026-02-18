package com.zennitech.digital.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnidadMedidaModel {
    private Integer id;
    private String codigo;
    private String descripcion;
    private Boolean activo;
}
