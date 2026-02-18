package com.zennitech.digital.controller;

import com.zennitech.digital.model.CompraModel;
import com.zennitech.digital.pojo.PageResponse;
import com.zennitech.digital.service.CompraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraController {
    private final CompraService compraService;

    @PostMapping
    public ResponseEntity<?> registrarCompra(@RequestBody CompraModel compra) {

        Long id = compraService.registrarCompra(compra);
        System.out.println(compra);
        return ResponseEntity.ok()
                .body("Compra registrada correctamente con ID: " + id);
    }

    @GetMapping
    public ResponseEntity<?> listarCompras(
            @RequestParam(required = false, defaultValue = "") String tipoDocumento,
            @RequestParam(required = false, defaultValue = "") String numeroDocumento,
            @RequestParam(required = false, defaultValue = "") String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            PageResponse<CompraModel> response = compraService.listarCompras(
                    tipoDocumento,
                    numeroDocumento,
                    estado,
                    page,
                    size
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al listar compras: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<?> finalizarCompra(@PathVariable Long id) {
        try {
            CompraModel compraFinalizada = compraService.finalizarCompra(id);
            return ResponseEntity.ok(compraFinalizada);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al finalizar la compra: " + e.getMessage()));
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCompraPorId(@PathVariable Long id) {
        try {
         //   log.info("Obteniendo compra con ID: {}", id);

            CompraModel compra = compraService.obtenerCompraPorId(id);

            return ResponseEntity.ok(compra);

        } catch (RuntimeException e) {
          //  log.error("Error al obtener compra: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
         //   log.error("Error al obtener compra", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al obtener la compra: " + e.getMessage()));
        }
    }
/*
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCompra(
            @PathVariable Long id,
            @RequestBody CompraModel compra) {
        try {
            //log.info("Actualizando compra con ID: {}", id);

            CompraModel compraActualizada = compraService.actualizarCompra(id, compra);

            return ResponseEntity.ok(compraActualizada);

        } catch (RuntimeException e) {
           // log.error("Error de validación: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
          //  log.error("Error al actualizar compra", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al actualizar la compra: " + e.getMessage()));
        }
    }*/
    record ErrorResponse(String message) {}
}
