package com.zennitech.digital.service;

import com.zennitech.digital.pojo.DespachoDTO;

import java.io.ByteArrayOutputStream;

public interface DespachoPdfService {
    /**
     * Genera un PDF de despacho basado en el ID del movimiento
     *
     * @param movimientoId ID del movimiento de stock
     * @return ByteArrayOutputStream con el contenido del PDF
     * @throws Exception si hay error al generar el PDF
     */
    ByteArrayOutputStream generarPdfDespacho(Long movimientoId) throws Exception;

    /**
     * Genera un PDF de despacho desde un DTO
     *
     * @param despachoDTO Datos del despacho
     * @return ByteArrayOutputStream con el contenido del PDF
     * @throws Exception si hay error al generar el PDF
     */
    ByteArrayOutputStream generarPdfDesdeDTO(DespachoDTO despachoDTO) throws Exception;

    /**
     * Obtiene los datos del despacho para generar el PDF
     *
     * @param movimientoId ID del movimiento de stock
     * @return DespachoDTO con los datos
     */
    DespachoDTO obtenerDatosDespacho(Long movimientoId);
}
