package com.zennitech.digital.service;

import com.zennitech.digital.model.MaterialDetalleModel;
import com.zennitech.digital.pojo.PageResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MaterialDetalleService {
    PageResponse<MaterialDetalleModel> listarTodos(int page, int size, String codigo, String descripcion);
    Optional<MaterialDetalleModel> obtenerPorId(Long id);
    List<MaterialDetalleModel> obtenerPorMaterialId(Long materialId);
    int guardar(MaterialDetalleModel detalle);
    int actualizar(Long id, MaterialDetalleModel detalle);
    int eliminar(Long id);

    int importarSeries(List<MaterialDetalleModel> series);
}
