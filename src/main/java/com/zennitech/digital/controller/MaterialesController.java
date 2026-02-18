package com.zennitech.digital.controller;

import com.zennitech.digital.model.MaterialDetalleModel;
import com.zennitech.digital.model.MaterialModel;
import com.zennitech.digital.pojo.PageResponse;
import com.zennitech.digital.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materiales")
@RequiredArgsConstructor
public class MaterialesController {
    private final MaterialService service;

    // Listar to DOS
    @GetMapping
    public ResponseEntity<List<MaterialModel>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    // 🔹 Buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<MaterialModel> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Crear
    @PostMapping
    public ResponseEntity<Integer> crear(@RequestBody MaterialModel material) {
        return ResponseEntity.ok(service.crear(material));
    }

    // 🔹 Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Integer> actualizar(@PathVariable Long id, @RequestBody MaterialModel material) {
        return ResponseEntity.ok(service.actualizar(id, material));
    }

    // 🔹 Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/listar-por-codigo")
    public ResponseEntity<PageResponse<MaterialModel>> listarSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String codigo,        // código de material
            @RequestParam(required = false) String descripcion    // búsqueda por número de serie
    ) {
        PageResponse<MaterialModel> resultado = service.listarPorCodigo(page, size, codigo, descripcion);
        return ResponseEntity.ok(resultado);
    }

}
