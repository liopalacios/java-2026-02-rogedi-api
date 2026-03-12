package com.zennitech.digital.service.impl;

import com.zennitech.digital.model.MaterialDetalleModel;
import com.zennitech.digital.model.MaterialModel;
import com.zennitech.digital.pojo.PageResponse;
import com.zennitech.digital.repository.MaterialRepository;
import com.zennitech.digital.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {
    private final MaterialRepository repository;

    public List<MaterialModel> listar() {
        return repository.findAll();
    }

    public Optional<MaterialModel> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public int crear(MaterialModel material) {
        material.setCreadoEn(LocalDateTime.now());
        material.setCreadoPor("SYSTEM");
        return repository.save(material);
    }

    public int actualizar(Long id, MaterialModel materialActualizado) {
        return repository.findById(id).map(m -> {
            m.setCodigo(materialActualizado.getCodigo());
            m.setDescripcion(materialActualizado.getDescripcion());
            m.setCreadoPor("SYSTEM");
            m.setActualizadoPor(materialActualizado.getActualizadoPor());
            m.setActualizadoEn(LocalDateTime.now());
            return repository.save(m);
        }).orElseThrow(() -> new RuntimeException("Material no encontrado con id " + id));
    }

    public void eliminar(Long id) {
        System.out.println(id);
        repository.deleteById(id);
    }

    @Override
    public PageResponse<MaterialModel> listarPorCodigo(int page, int size, String codigo, String descripcion) {
        List<MaterialModel> data = repository.listarPorCodigo(
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
        System.out.println(total);
        return new PageResponse<>(data, total, page,1,page, size,page >= (1) - 1);

    }
}
