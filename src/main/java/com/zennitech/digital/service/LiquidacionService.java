package com.zennitech.digital.service;

import com.zennitech.digital.model.LiquidacionModel;
import com.zennitech.digital.pojo.PageResponse;

public interface LiquidacionService {
    PageResponse<LiquidacionModel> listarLiquidacionesPaginadas(
            String coLiquidacion,
            String dniCliente,
            String nombreCliente,
            String tipoInstalacionCodigo,
            String estadoRevisionCodigo,
            String tipoPropiedadCodigo,
            String fechaDesde,
            String fechaHasta,
            int page,
            int size);

    boolean guardarLiquidacion(LiquidacionModel liquidacion);
}
