package com.zennitech.digital.service.impl;

import com.zennitech.digital.model.*;
import com.zennitech.digital.pojo.PageResponse;
import com.zennitech.digital.repository.StockRepository;
import com.zennitech.digital.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;
    @Override
    @Transactional(readOnly = true)
    public PageResponse<StockGeneralModel> listarStockGeneral(String contratista, String condicion, String zonal,
                                                              String sap, String articulo, int page, int size) {
        log.info("Listando stock general - page: {}, size: {}", page, size);

        // Obtener datos paginados
        List<StockGeneralModel> stock = stockRepository.listarStockGeneral(
                contratista, condicion, zonal, sap, articulo, page, size
        );

        // Contar total
        int totalElements = stockRepository.contarStockGeneral(
                contratista, condicion, zonal, sap, articulo
        );

        // Calcular total de páginas
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // Crear respuesta
        PageResponse<StockGeneralModel> response = new PageResponse<>();
        response.setContent(stock);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        response.setCurrentPage(page);
        response.setPageSize(size);
        response.setLast(page >= totalPages - 1);

        log.info("Stock listado - Total elementos: {}", totalElements);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContratistaModel> listarContratistas() {
        return stockRepository.listarContratistas();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ZonalModel> listarZonales() {
        return stockRepository.listarZonales();
    }

    @Override
    public PageResponse<StockAtendidoModel> listarStockAtendidos(String contratista, String condicion, String zonal,
                                                                 String sap, String articulo, String serie,
                                                                 String tecnico, String tipoMovimiento, String fechaDesde,
                                                                 String fechaHasta, int page, int size) {
        log.info("Listando stock series - page: {}, size: {}", page, size);

        // Obtener datos paginados
        List<StockAtendidoModel> series = stockRepository.listarMovimientosStock(Arrays.asList(1, 2, 5),
                contratista, condicion, zonal, sap, articulo, serie,tecnico,tipoMovimiento, fechaDesde, fechaHasta,
                page,size
        );

        // Contar total
        int totalElements = stockRepository.contarStockSeries(
                contratista, condicion, zonal, sap, articulo, serie
        );

        // Calcular total de páginas
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // Crear respuesta
        PageResponse<StockAtendidoModel> response = new PageResponse<>();
        response.setContent(series);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        response.setCurrentPage(page);
        response.setPageSize(size);
        response.setLast(page >= totalPages - 1);

        log.info("Stock series listado - Total elementos: {}", totalElements);

        return response;
    }

    @Override
    public PageResponse<StockAtendidoModel> listarStockDevueltos(String contratista, String condicion, String zonal,
                                                                 String sap, String articulo, String serie, String tecnico,
                                                                 String tipoMovimiento, String fechaDesde, String fechaHasta,
                                                                 int page, int size) {
        log.info("Listando stock series - page: {}, size: {}", page, size);

        // Obtener datos paginados
        List<StockAtendidoModel> series = stockRepository.listarMovimientosStock(Arrays.asList(3,4),
                contratista, condicion, zonal, sap, articulo, serie,tecnico,tipoMovimiento, fechaDesde, fechaHasta,
                page,size
        );

        // Contar total
        int totalElements = stockRepository.contarStockSeries(
                contratista, condicion, zonal, sap, articulo, serie
        );

        // Calcular total de páginas
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // Crear respuesta
        PageResponse<StockAtendidoModel> response = new PageResponse<>();
        response.setContent(series);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        response.setCurrentPage(page);
        response.setPageSize(size);
        response.setLast(page >= totalPages - 1);

        log.info("Stock series listado - Total elementos: {}", totalElements);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StockSerieModel> listarStockSeries(
            String contratista,
            String condicion,
            String zonal,
            String sap,
            String articulo,
            String serie,
            int page,
            int size) {

        log.info("Listando stock series - page: {}, size: {}", page, size);

        // Obtener datos paginados
        List<StockSerieModel> series = stockRepository.listarStockSeries(
                contratista, condicion, zonal, sap, articulo, serie, page, size
        );

        // Contar total
        int totalElements = stockRepository.contarStockSeries(
                contratista, condicion, zonal, sap, articulo, serie
        );

        // Calcular total de páginas
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // Crear respuesta
        PageResponse<StockSerieModel> response = new PageResponse<>();
        response.setContent(series);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        response.setCurrentPage(page);
        response.setPageSize(size);
        response.setLast(page >= totalPages - 1);

        log.info("Stock series listado - Total elementos: {}", totalElements);

        return response;
    }
}
