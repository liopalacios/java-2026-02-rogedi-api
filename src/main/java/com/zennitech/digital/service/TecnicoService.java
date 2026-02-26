package com.zennitech.digital.service;

import com.zennitech.digital.model.TecnicoModel;

import java.util.List;

public interface TecnicoService {
    /**
     * Crear un nuevo técnico
     * @param tecnico datos del técnico
     * @return técnico creado
     */
    int crearTecnico(TecnicoModel tecnico);

    /**
     * Modificar un técnico existente
     * @param id identificador del técnico
     * @param tecnico datos actualizados
     * @return técnico modificado
     */
    int modificarTecnico(Long id, TecnicoModel tecnico);

    /**
     * Listar todos los técnicos
     * @return lista de técnicos
     */
    List<TecnicoModel> listarTecnicos();

    /**
     * Buscar técnicos por nombre o documento
     * @param nombre nombre (puede ser null)
     * @param documento número de documento (puede ser null)
     * @return lista de técnicos que cumplen con el filtro
     */
    List<TecnicoModel> buscarTecnico(String nombre, String documento, String tipo);

    /**
     * Desactivar (borrado lógico) un técnico
     * @param id identificador del técnico
     */
    int desactivarTecnico(Long id);
}
