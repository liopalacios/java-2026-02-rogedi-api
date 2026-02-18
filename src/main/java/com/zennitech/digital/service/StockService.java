package com.zennitech.digital.service;

import com.zennitech.digital.model.*;
import com.zennitech.digital.pojo.PageResponse;

import java.util.List;

public interface StockService {
    PageResponse<StockGeneralModel> listarStockGeneral(
            String contratista,
            String condicion,
            String zonal,
            String sap,
            String articulo,
            int page,
            int size
    );

    PageResponse<StockSerieModel> listarStockSeries(
            String contratista,
            String condicion,
            String zonal,
            String sap,
            String articulo,
            String serie,
            int page,
            int size
    );

    List<ContratistaModel> listarContratistas();

    List<ZonalModel> listarZonales();

    PageResponse<StockAtendidoModel> listarStockAtendidos(String contratista, String condicion, String zonal,
                                                          String sap, String articulo, String serie, String tecnico,
                                                          String tipoMovimiento, String fechaDesde, String fechaHasta,
                                                          int page, int size);

    PageResponse<StockAtendidoModel> listarStockDevueltos(String contratista, String condicion, String zonal, String sap,
                                                          String articulo, String serie, String tecnico,
                                                          String tipoMovimiento, String fechaDesde, String fechaHasta,
                                                          int page, int size);
}
