package com.zennitech.digital.service.impl;

import com.zennitech.digital.model.CompraDetalleModel;
import com.zennitech.digital.model.CompraModel;
import com.zennitech.digital.model.MaterialDetalleModel;
import com.zennitech.digital.pojo.PageResponse;
import com.zennitech.digital.pojo.SerieInfoDTO;
import com.zennitech.digital.repository.CompraRepository;
import com.zennitech.digital.service.CompraService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompraServiceImpl implements CompraService {
    private final CompraRepository compraRepository;

    @Override
    @Transactional
    public Long registrarCompra(CompraModel compra) {

        List<String> seriesDuplicadas = validarSeriesDuplicadas(compra);
        if (!seriesDuplicadas.isEmpty()) {
            throw new RuntimeException(
                    "Las siguientes series ya existen con estado Disponible: " +
                            String.join(", ", seriesDuplicadas)
            );
        }
        // 1️⃣ Guardar cabecera
        Long compraId = compraRepository.saveCompra(compra);

        // 2️⃣ Guardar detalle + series
        compra.getMateriales().forEach(detalle -> {

            detalle.setIdCompra(compraId);
            System.out.print(detalle);
            Long detalleId = compraRepository.saveDetalle(detalle);

            if (detalle.getSeries() != null) {
                detalle.getSeries().forEach(serie ->
                        compraRepository.saveSerie(
                                detalleId,
                                detalle.getMaterialId(),
                                serie,
                                compra.getFechaIngreso(),
                                detalle.getUsuarioCreacion()
                        )
                );
            }
        });

        return compraId;
    }
    /**
     * Valida que ninguna serie esté duplicada con estado Disponible
     */
    private List<String> validarSeriesDuplicadas(CompraModel compra) {
        List<String> duplicadas = new ArrayList<>();

        for (CompraDetalleModel detalle : compra.getMateriales()) {
            if (detalle.getSeries() == null) continue;

            for (String serie : detalle.getSeries()) {
                if (serie == null || serie.trim().isEmpty()) continue;

                if (compraRepository.existeSerieDisponible(serie.trim())) {
                    SerieInfoDTO info = compraRepository.obtenerInfoSerie(serie.trim());

                    if (info != null) {
                        duplicadas.add(String.format(
                                "%s (Material: %s - %s)",
                                serie,
                                info.getCodigoMaterial(),
                                info.getDescripcionMaterial()
                        ));
                    } else {
                        duplicadas.add(serie);
                    }
                }
            }
        }

        return duplicadas;
    }
    @Override
    @Transactional(readOnly = true)
    public PageResponse<CompraModel> listarCompras(
            String tipoDocumento,
            String numeroDocumento,
            String estado,
            int page,
            int size) {

        // Obtener compras con paginación
        List<CompraModel> compras = compraRepository.listarCompras(
                tipoDocumento,
                numeroDocumento,
                estado,
                page,
                size
        );

        // Contar total de compras
        int totalElements = compraRepository.contarCompras(
                tipoDocumento,
                numeroDocumento,
                estado
        );

        // Calcular total de páginas
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // Crear respuesta
        PageResponse<CompraModel> response = new PageResponse<>();
        response.setContent(compras);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        response.setCurrentPage(page);
        response.setPageSize(size);
        response.setLast(page >= totalPages - 1);

        return response;
    }

    @Override
    @Transactional
    public CompraModel finalizarCompra(Long id) {
        // Buscar compra
        CompraModel compra = compraRepository.findById(id);

        if (compra == null) {
            throw new RuntimeException("Compra no encontrada con ID: " + id);
        }

        if ("F".equals(compra.getEstado())) {
            throw new RuntimeException("La compra ya está finalizada");
        }

        // Finalizar compra
        int filasAfectadas = compraRepository.finalizarCompra(id);

        if (filasAfectadas == 0) {
            throw new RuntimeException("No se pudo finalizar la compra");
        }

        // Retornar compra actualizada
        compra.setEstado("F");
        return compra;
    }

    @Override
    @Transactional(readOnly = true)
    public CompraModel obtenerCompraPorId(Long id) {
        CompraModel compra = compraRepository.findByIdWithDetails(id);

        if (compra == null) {
            throw new RuntimeException("Compra no encontrada con ID: " + id);
        }

        return compra;
    }
/*
    @Override
    @Transactional
    public CompraModel actualizarCompra(Long id, CompraModel compra) {
        // Verificar que exista
        CompraModel compraExistente = compraRepository.findById(id);

        if (compraExistente == null) {
            throw new RuntimeException("Compra no encontrada con ID: " + id);
        }

        if (!"P".equals(compraExistente.getEstado())) {
            throw new RuntimeException("Solo se pueden modificar compras en estado Pendiente");
        }

        // Actualizar compra
        int filasAfectadas = compraRepository.updateCompra(id, compra);

        if (filasAfectadas == 0) {
            throw new RuntimeException("No se pudo actualizar la compra");
        }

        // TODO: Actualizar materiales y series si es necesario

        return compraRepository.findByIdWithDetails(id);
    }*/
}
