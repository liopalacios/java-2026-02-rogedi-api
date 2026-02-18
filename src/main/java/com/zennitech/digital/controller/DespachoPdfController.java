package com.zennitech.digital.controller;

import com.zennitech.digital.service.DespachoPdfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;

@Slf4j
@RestController
@RequestMapping("/api/despacho")
@RequiredArgsConstructor
public class DespachoPdfController {
    private final DespachoPdfService despachoPdfService;

    /**
     * Genera y descarga el PDF de despacho
     *
     * GET /api/despacho/pdf/{movimientoId}
     *
     * @param movimientoId ID del movimiento de stock
     * @return PDF como archivo descargable
     */
    @GetMapping("/pdf/{movimientoId}")
    public ResponseEntity<byte[]> generarPdfDespacho(@PathVariable Long movimientoId) {
        log.info("Solicitud de PDF para movimiento ID: {}", movimientoId);

        try {
            // Generar PDF
            ByteArrayOutputStream pdfStream = despachoPdfService.generarPdfDespacho(movimientoId);

            // Preparar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            /*headers.setContentDispositionFormData(
                    "inline",
                    "despacho_" + movimientoId + ".pdf"
            );*/
            headers.setContentDisposition(
                    ContentDisposition.inline()
                            .filename("despacho_" + movimientoId + ".pdf")
                            .build()
            );
            //headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);

            log.info("PDF generado exitosamente para movimiento ID: {}", movimientoId);

            return new ResponseEntity<>(pdfStream.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error al generar PDF para movimiento ID: " + movimientoId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Genera y devuelve el PDF como vista previa (inline)
     *
     * GET /api/despacho/preview/{movimientoId}
     *
     * @param movimientoId ID del movimiento de stock
     * @return PDF para visualización en navegador
     */
    @GetMapping("/preview/{movimientoId}")
    public ResponseEntity<byte[]> previsualizarPdfDespacho(@PathVariable Long movimientoId) {
        log.info("Solicitud de previsualización de PDF para movimiento ID: {}", movimientoId);

        try {
            // Generar PDF
            ByteArrayOutputStream pdfStream = despachoPdfService.generarPdfDespacho(movimientoId);

            // Preparar headers para visualización inline
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "inline; filename=despacho_" + movimientoId + ".pdf");

            log.info("PDF de previsualización generado exitosamente para movimiento ID: {}", movimientoId);

            return new ResponseEntity<>(pdfStream.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error al generar PDF de previsualización para movimiento ID: " + movimientoId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para verificar si existe un despacho
     *
     * GET /api/despacho/existe/{movimientoId}
     *
     * @param movimientoId ID del movimiento
     * @return true si existe, false si no
     */
    @GetMapping("/existe/{movimientoId}")
    public ResponseEntity<Boolean> existeDespacho(@PathVariable Long movimientoId) {
        log.info("Verificando existencia de despacho para movimiento ID: {}", movimientoId);

        try {
            var despacho = despachoPdfService.obtenerDatosDespacho(movimientoId);
            return ResponseEntity.ok(despacho != null);
        } catch (Exception e) {
            log.error("Error al verificar despacho", e);
            return ResponseEntity.ok(false);
        }
    }
}
