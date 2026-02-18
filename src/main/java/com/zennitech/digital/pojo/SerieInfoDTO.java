package com.zennitech.digital.pojo;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SerieInfoDTO {
    private String numeroSerie;
    private String estado;
    private Long materialId;
    private String codigoMaterial;
    private String descripcionMaterial;
}
