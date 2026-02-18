package com.zennitech.digital.exception;

import lombok.Getter;

@Getter
public class SerieDuplicadaException extends RuntimeException {
    private final String numeroSerie;
    private final String codigoMaterial;
    private final String descripcionMaterial;

    public SerieDuplicadaException(String numeroSerie, String codigoMaterial, String descripcionMaterial) {
        super();
        this.numeroSerie = numeroSerie;
        this.codigoMaterial = codigoMaterial;
        this.descripcionMaterial = descripcionMaterial;
    }
}
