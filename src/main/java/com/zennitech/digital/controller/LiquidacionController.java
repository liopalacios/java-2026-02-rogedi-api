package com.zennitech.digital.controller;

import com.zennitech.digital.model.LiquidacionModel;
import com.zennitech.digital.pojo.PageResponse;
import com.zennitech.digital.service.LiquidacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/api/liquidaciones")
@RequiredArgsConstructor
public class LiquidacionController {
    private final LiquidacionService service;

    @GetMapping
    public ResponseEntity<PageResponse<LiquidacionModel>> listarLiquidaciones(
            @RequestParam(required = false) String coLiquidacion,
            @RequestParam(required = false) String dniCliente,
            @RequestParam(required = false) String nombreCliente,
            @RequestParam(required = false) String tipoInstalacion,
            @RequestParam(required = false) String estadoRevision,
            @RequestParam(required = false) String tipoPropiedad,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        // Los parámetros ahora reciben códigos en lugar de IDs
        PageResponse<LiquidacionModel> resultado = service.listarLiquidacionesPaginadas(
                coLiquidacion, dniCliente, nombreCliente,
                tipoInstalacion, estadoRevision, tipoPropiedad,
                fechaDesde, fechaHasta, page, size
        );

        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearLiquidacion(
            @RequestBody LiquidacionModel liquidacion) {

        try {
            boolean success = service.guardarLiquidacion(liquidacion);
            return ResponseEntity.ok(Map.of("success", success));
        } catch (Exception e) {
            log.error("Error al crear liquidación", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
