package com.zennitech.digital.controller;

import com.zennitech.digital.model.*;
import com.zennitech.digital.pojo.PageResponse;
import com.zennitech.digital.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @GetMapping("/general")
    public ResponseEntity<?> listarStockGeneral(
            @RequestParam(required = false, defaultValue = "") String contratista,
            @RequestParam(required = false, defaultValue = "") String condicion,
            @RequestParam(required = false, defaultValue = "") String zonal,
            @RequestParam(required = false, defaultValue = "") String sap,
            @RequestParam(required = false, defaultValue = "") String articulo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            log.info("GET /api/stock/general - Params: contratista={}, condicion={}, zonal={}, sap={}, articulo={}, page={}, size={}",
                    contratista, condicion, zonal, sap, articulo, page, size);

            PageResponse<StockGeneralModel> response = stockService.listarStockGeneral(
                    contratista, condicion, zonal, sap, articulo, page, size
            );

            log.info("Stock general listado exitosamente: {} registros", response.getTotalElements());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al listar stock general", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            "Error al listar stock general: " + e.getMessage(),
                            "INTERNAL_ERROR"
                    ));
        }
    }

    @GetMapping("/series")
    public ResponseEntity<?> listarStockSeries(
            @RequestParam(required = false, defaultValue = "") String contratista,
            @RequestParam(required = false, defaultValue = "") String condicion,
            @RequestParam(required = false, defaultValue = "") String zonal,
            @RequestParam(required = false, defaultValue = "") String sap,
            @RequestParam(required = false, defaultValue = "") String articulo,
            @RequestParam(required = false, defaultValue = "") String serie,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            log.info("GET /api/stock/series - Params: contratista={}, condicion={}, zonal={}, sap={}, articulo={}, serie={}, page={}, size={}",
                    contratista, condicion, zonal, sap, articulo, serie, page, size);

            PageResponse<StockSerieModel> response = stockService.listarStockSeries(
                    contratista, condicion, zonal, sap, articulo, serie, page, size
            );

            log.info("Stock series listado exitosamente: {} registros", response.getTotalElements());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al listar stock series", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            "Error al listar stock series: " + e.getMessage(),
                            "INTERNAL_ERROR"
                    ));
        }
    }

    @GetMapping("/atendidos")
    public ResponseEntity<?> listarStockAtendidos(
            @RequestParam(required = false, defaultValue = "") String contratista,
            @RequestParam(required = false, defaultValue = "") String condicion,
            @RequestParam(required = false, defaultValue = "") String zonal,
            @RequestParam(required = false, defaultValue = "") String sap,
            @RequestParam(required = false, defaultValue = "") String articulo,
            @RequestParam(required = false, defaultValue = "") String serie,
            @RequestParam(required = false, defaultValue = "") String tecnico,
            @RequestParam(required = false, defaultValue = "") String tipoMovimiento,
            @RequestParam(required = false, defaultValue = "") String fechaDesde,
            @RequestParam(required = false, defaultValue = "") String fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            log.info("GET /api/stock/atendidos - Params: contratista={}, condicion={}, zonal={}, sap={}, articulo={}, serie={}, tecnico={}, tipoMovimiento={}, fechaDesde={}, fechaHasta={}, page={}, size={}",
                    contratista, condicion, zonal, sap, articulo, serie, tecnico, tipoMovimiento, fechaDesde, fechaHasta, page, size);

            PageResponse<StockAtendidoModel> response = stockService.listarStockAtendidos(
                    contratista, condicion, zonal, sap, articulo, serie, tecnico, tipoMovimiento, fechaDesde, fechaHasta, page, size
            );

            log.info("Stock atendidos listado exitosamente: {} registros", response.getTotalElements());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al listar stock atendidos", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            "Error al listar stock atendidos: " + e.getMessage(),
                            "INTERNAL_ERROR"
                    ));
        }
    }
    @GetMapping("/devueltos")
    public ResponseEntity<?> listarStockDevueltos(
            @RequestParam(required = false, defaultValue = "") String contratista,
            @RequestParam(required = false, defaultValue = "") String condicion,
            @RequestParam(required = false, defaultValue = "") String zonal,
            @RequestParam(required = false, defaultValue = "") String sap,
            @RequestParam(required = false, defaultValue = "") String articulo,
            @RequestParam(required = false, defaultValue = "") String serie,
            @RequestParam(required = false, defaultValue = "") String tecnico,
            @RequestParam(required = false, defaultValue = "") String tipoMovimiento,
            @RequestParam(required = false, defaultValue = "") String fechaDesde,
            @RequestParam(required = false, defaultValue = "") String fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            log.info("GET /api/stock/atendidos - Params: contratista={}, condicion={}, zonal={}, sap={}, articulo={}, serie={}, tecnico={}, tipoMovimiento={}, fechaDesde={}, fechaHasta={}, page={}, size={}",
                    contratista, condicion, zonal, sap, articulo, serie, tecnico, tipoMovimiento, fechaDesde, fechaHasta, page, size);

            PageResponse<StockAtendidoModel> response = stockService.listarStockDevueltos(
                    contratista, condicion, zonal, sap, articulo, serie, tecnico, tipoMovimiento, fechaDesde, fechaHasta, page, size
            );

            log.info("Stock atendidos listado exitosamente: {} registros", response.getTotalElements());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al listar stock atendidos", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            "Error al listar stock atendidos: " + e.getMessage(),
                            "INTERNAL_ERROR"
                    ));
        }
    }

    @GetMapping("/contratistas")
    public ResponseEntity<?> listarContratistas() {
        try {
            log.info("GET /api/stock/contratistas");

            List<ContratistaModel> contratistas = stockService.listarContratistas();

            return ResponseEntity.ok(contratistas);

        } catch (Exception e) {
            log.error("Error al listar contratistas", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            "Error al listar contratistas: " + e.getMessage(),
                            "INTERNAL_ERROR"
                    ));
        }
    }

    @GetMapping("/zonales")
    public ResponseEntity<?> listarZonales() {
        try {
            log.info("GET /api/stock/zonales");

            List<ZonalModel> zonales = stockService.listarZonales();

            return ResponseEntity.ok(zonales);

        } catch (Exception e) {
            log.error("Error al listar zonales", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            "Error al listar zonales: " + e.getMessage(),
                            "INTERNAL_ERROR"
                    ));
        }
    }

    record ErrorResponse(String message, String error) {}
}
