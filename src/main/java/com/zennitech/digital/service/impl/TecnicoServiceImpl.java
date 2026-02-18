package com.zennitech.digital.service.impl;

import com.zennitech.digital.model.TecnicoModel;
import com.zennitech.digital.repository.TecnicoJdbcRepository;
import com.zennitech.digital.service.TecnicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class TecnicoServiceImpl implements TecnicoService {

    private final TecnicoJdbcRepository tecnicoRepository;
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public int crearTecnico(TecnicoModel tecnico) {

        return tecnicoRepository.crear(tecnico);
    }

    @Override
    public int modificarTecnico(Long id, TecnicoModel tecnico) {
        return tecnicoRepository.modificar(id, tecnico);
    }

    @Override
    public List<TecnicoModel> listarTecnicos() {
        return tecnicoRepository.listar();
    }

    @Override
    public List<TecnicoModel> buscarTecnico(String nombre, String documento) {
        return tecnicoRepository.buscarPorNombreODocumento(nombre, documento);
    }

    @Override
    public int desactivarTecnico(Long id) {
        return tecnicoRepository.desactivar(id);
    }
}
