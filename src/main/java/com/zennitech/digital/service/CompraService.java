package com.zennitech.digital.service;

import com.zennitech.digital.model.CompraModel;
import com.zennitech.digital.pojo.PageResponse;

public interface CompraService {
    Long registrarCompra(CompraModel compra);
    PageResponse<CompraModel> listarCompras(
            String tipoDocumento,
            String numeroDocumento,
            String estado,
            int page,
            int size
    );

    CompraModel finalizarCompra(Long id);
    CompraModel obtenerCompraPorId(Long id);
}
