package com.zennitech.digital.controller;

import com.zennitech.digital.model.TecnicoModel;
import com.zennitech.digital.service.TecnicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tecnicos")
public class TecnicoController {
    private final TecnicoService tecnicoService;
    /**
     * Crear técnico
     */
    @PostMapping
    public ResponseEntity<Integer> crearTecnico(@RequestBody TecnicoModel tecnico) {
        return ResponseEntity.ok(tecnicoService.crearTecnico(tecnico));
    }

    /**
     * Modificar técnico
     */
    @PutMapping("/{id}")
    public ResponseEntity<Integer> modificarTecnico(@PathVariable Long id, @RequestBody TecnicoModel tecnico) {
        return ResponseEntity.ok(tecnicoService.modificarTecnico(id, tecnico));
    }

    /**
     * Listar todos los técnicos
     */
    @GetMapping
    public ResponseEntity<List<TecnicoModel>> listarTecnicos() {
        return ResponseEntity.ok(tecnicoService.listarTecnicos());
    }

    /**
     * Buscar técnicos por nombre o documento
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<TecnicoModel>> buscarTecnico(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String documento) {
        return ResponseEntity.ok(tecnicoService.buscarTecnico(nombre, documento, tipo));
    }

    /**
     * Desactivar técnico (borrado lógico)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> desactivarTecnico(@PathVariable Long id) {
        int rpta = tecnicoService.desactivarTecnico(id);
        System.out.println(rpta);
        return ResponseEntity.ok(rpta);
    }
}
