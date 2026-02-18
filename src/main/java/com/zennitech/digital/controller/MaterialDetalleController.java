package com.zennitech.digital.controller;

import com.zennitech.digital.model.MaterialDetalleModel;
import com.zennitech.digital.pojo.PageResponse;
import com.zennitech.digital.service.MaterialDetalleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/materiales-detalle")
@RequiredArgsConstructor
public class MaterialDetalleController {
    private final MaterialDetalleService service;

    /*@GetMapping
    public ResponseEntity<PageResponse<MaterialDetalleModel>> listarTodos(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(required = false) String codigo,
                                                            @RequestParam(required = false) String descripcion) {
        return ResponseEntity.ok(service.listarTodos(page, size, codigo, descripcion));


    }*/
    @GetMapping
    public ResponseEntity<PageResponse<MaterialDetalleModel>> listarSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String codigo,        // código de material
            @RequestParam(required = false) String descripcion    // búsqueda por número de serie
    ) {
        PageResponse<MaterialDetalleModel> resultado = service.listarTodos(page, size, codigo, descripcion);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialDetalleModel> obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/material/{materialId}")
    public ResponseEntity<List<MaterialDetalleModel>> obtenerPorMaterialId(@PathVariable Long materialId) {
        return ResponseEntity.ok(service.obtenerPorMaterialId(materialId));
    }
    @PostMapping
    public ResponseEntity<Integer> guardar(@RequestBody MaterialDetalleModel detalle) {
        return ResponseEntity.ok(service.guardar(detalle));
    }

    @PostMapping("/importar")
    public ResponseEntity<Map<String, Object>> importarSeries(@RequestBody List<MaterialDetalleModel> series) {
        try {
            // Validar que la lista no esté vacía
            if (series == null || series.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "La lista de series está vacía");
                error.put("importadas", 0);
                return ResponseEntity.badRequest().body(error);
            }

            // Llamar al servicio para guardar todas las series
            int seriesImportadas = service.importarSeries(series);

            // Respuesta exitosa
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Series importadas correctamente");
            response.put("importadas", seriesImportadas);
            response.put("total", series.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Manejo de errores
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al importar series: " + e.getMessage());
            error.put("importadas", 0);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> actualizar(@PathVariable Long id, @RequestBody MaterialDetalleModel detalle) {
        return ResponseEntity.ok(service.actualizar(id, detalle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(service.eliminar(id));
    }

}
