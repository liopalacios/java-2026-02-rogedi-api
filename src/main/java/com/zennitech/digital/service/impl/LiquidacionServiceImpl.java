package com.zennitech.digital.service.impl;

import com.zennitech.digital.model.LiquidacionModel;
import com.zennitech.digital.pojo.PageResponse;
import com.zennitech.digital.repository.LiquidacionRepository;
import com.zennitech.digital.service.LiquidacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiquidacionServiceImpl implements LiquidacionService {
    private final LiquidacionRepository repository;
    @Override
    public PageResponse<LiquidacionModel> listarLiquidacionesPaginadas(String coLiquidacion, String dniCliente,
                                                                       String nombreCliente, String tipoInstalacionCodigo,
                                                                       String estadoRevisionCodigo, String tipoPropiedadCodigo,
                                                                       String fechaDesde, String fechaHasta, int page, int size) {

        try {
            List<LiquidacionModel> contenido = repository.listarLiquidaciones(
                    coLiquidacion, dniCliente, nombreCliente,
                    tipoInstalacionCodigo, estadoRevisionCodigo, tipoPropiedadCodigo,
                    fechaDesde, fechaHasta, page, size
            );

            int total = repository.contarLiquidaciones(
                    coLiquidacion, dniCliente, nombreCliente,
                    tipoInstalacionCodigo, estadoRevisionCodigo, tipoPropiedadCodigo,
                    fechaDesde, fechaHasta
            );

            return PageResponse.<LiquidacionModel>builder()
                    .content(contenido)
                    .totalElements(total)
                    .page(page)
                    .pageSize(size)
                    .totalPages((int) Math.ceil((double) total / size))
                    .build();

        } catch (Exception e) {
            log.error("Error en servicio al listar liquidaciones", e);
            return new PageResponse<>();
        }


    }

    @Override
    public boolean guardarLiquidacion(LiquidacionModel liquidacion) {
        return repository.guardarLiquidacion(liquidacion);
    }
}
