package com.zennitech.digital.service.impl;

import com.zennitech.digital.model.MaterialDetalleModel;
import com.zennitech.digital.pojo.PageResponse;
import com.zennitech.digital.repository.MaterialDetalleRepository;
import com.zennitech.digital.service.MaterialDetalleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialDetalleServiceImpl implements MaterialDetalleService {
    private final MaterialDetalleRepository repository;
    @Override
    public PageResponse<MaterialDetalleModel> listarTodos(int page, int size, String codigo, String descripcion) {
        List<MaterialDetalleModel> data = repository.listar(
                codigo,
                descripcion,
                page,
                size
        );
        System.out.println(data);
        int total = repository.contar(
                codigo,
                descripcion
        );
        int totalPages = (int) Math.ceil((double) total / size);
        System.out.println(total);
        return new PageResponse<>(data, total, totalPages, page, page,size,page >= totalPages - 1);
    }

    @Override
    public Optional<MaterialDetalleModel> obtenerPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<MaterialDetalleModel> obtenerPorMaterialId(Long materialId) {
        return repository.findByMaterialId(materialId);
    }

    @Override
    public int guardar(MaterialDetalleModel detalle) {
        detalle.setCreadoEn(LocalDateTime.now());
        return repository.save(detalle);
    }

    @Override
    public int actualizar(Long id, MaterialDetalleModel detalle) {
        detalle.setActualizadoEn(LocalDateTime.now());
        return repository.update(id, detalle);
    }

    @Override
    public int eliminar(Long id) {
        return repository.delete(id);
    }

    @Override
    public int importarSeries(List<MaterialDetalleModel> series) {

        // Filtrar y preparar las series válidas
        List<MaterialDetalleModel> seriesValidas = series.stream()
                .filter(serie -> serie.getNumeroSerie() != null &&
                        !serie.getNumeroSerie().trim().isEmpty())
                .peek(serie -> {
                    // Establecer valores por defecto
                    if (serie.getEstado() == null || serie.getEstado().trim().isEmpty()) {
                        serie.setEstado("DISPONIBLE");
                    }
                    if (serie.getCreadoPor() == null) {
                        serie.setCreadoPor("SISTEMA_IMPORTACION");
                    }
                    if (serie.getCreadoEn() == null) {
                        serie.setCreadoEn(LocalDateTime.now());
                    }
                })
                .collect(Collectors.toList());

        if (seriesValidas.isEmpty()) {
            return 0;
        }

        // Guardar todas de una vez usando batch insert
        int[][] resultados = repository.saveAll(seriesValidas);

        // Contar cuántas se guardaron exitosamente
        int importadas = 0;
        for (int[] lote : resultados) {
            for (int resultado : lote) {
                if (resultado > 0) {
                    importadas++;
                }
            }
        }

        return importadas;
    }
}
