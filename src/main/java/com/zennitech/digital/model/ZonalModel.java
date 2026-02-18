package com.zennitech.digital.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZonalModel {
    private Integer id;
    private String codigo;
    private String nombre;
    private String region;
    private Boolean activo;
}
