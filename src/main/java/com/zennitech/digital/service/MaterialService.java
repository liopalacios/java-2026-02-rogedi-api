package com.zennitech.digital.service;

import com.zennitech.digital.model.MaterialDetalleModel;
import com.zennitech.digital.model.MaterialModel;
import com.zennitech.digital.pojo.PageResponse;

import java.util.List;
import java.util.Optional;

public interface MaterialService {
    List<MaterialModel> listar();
    Optional<MaterialModel> buscarPorId(Long id);
    int crear(MaterialModel material);
    int actualizar(Long id, MaterialModel materialActualizado);
    void eliminar(Long id);
    PageResponse<MaterialModel> listarPorCodigo(int page, int size, String codigo, String descripcion);
}
