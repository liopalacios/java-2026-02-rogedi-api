package com.zennitech.digital.repository;

import com.zennitech.digital.pojo.DespachoDTO;
import com.zennitech.digital.pojo.DespachoDetalleDTO;
import com.zennitech.digital.pojo.SerieInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class DespachoPdfRepository {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Obtener información de una serie existente
     */
    public DespachoDTO obtenerDatosDespacho(Long movimientoId) {
        log.info("Obteniendo datos del despacho para movimiento ID: {}", movimientoId);

        // Query principal para obtener datos del movimiento
        String sqlMovimiento = """
            SELECT 
                ms.id,
                ms.num_documento,
                ms.fec_movimiento,
                t.email AS tecnico_codigo,
                t.nombres || ' ' || t.apellidos AS tecnico_nombre,
                t.numero_documento AS tecnico_dni,
                z.nombre AS ciudad,
                c.razon_social AS empresa,
                c.ruc AS ruc,
                tm.nombre as nombre_tipo_movimiento,
                co.nombre as nombre_condicion
            FROM rogedibd.movimientos_stock ms
                LEFT JOIN rogedibd.tecnicos t ON ms.tecnico_id = t.id
                LEFT JOIN rogedibd.tipo_movimiento tm ON ms.tipo_movimiento_id = tm.id
                LEFT JOIN rogedibd.zonales z ON t.id_zonal = z.id
                LEFT JOIN rogedibd.materiales_detalle md ON ms.material_detalle_id = md.id
                LEFT JOIN rogedibd.materiales m ON ms.material_id = m.id
                LEFT JOIN rogedibd.contratistas c ON m.id_contratista = c.id
                left join rogedibd.condiciones co on co.id = ms.condicion_id
            WHERE ms.id = ?
            LIMIT 1
        """;
        // IMPRIMIR QUERY COMPLETA EN CONSOLA
        System.out.println("=== DEBUG QUERY MOVIMIENTO ===");
        System.out.println("SQL: " + sqlMovimiento);
        System.out.println("PARAM: movimientoId = " + movimientoId);
        System.out.println("=============================");
        Map<String, Object> movimiento = null;
        try {
            movimiento = jdbcTemplate.queryForMap(sqlMovimiento, movimientoId);
            System.out.println("RESULTADO: " + movimiento);
        } catch (Exception e) {
            System.err.println("ERROR en query: " + e.getMessage());
            // También puedes ejecutar la query manualmente en tu cliente SQL
            System.err.println("Ejecuta manualmente en SQL: ");
            System.err.println(sqlMovimiento.replace("?", movimientoId.toString()));
            throw e;
        }
        // Query para obtener los detalles (si existe tabla movimientos_stock_detalle)
        String sqlDetalles = """
            SELECT 
                ROW_NUMBER() OVER (ORDER BY msd.id) AS numero,
                m.codigo AS codigo_sap,
                m.descripcion,
                STRING_AGG(md.numero_serie, ', ') AS series,
                um.abreviacion AS unidad_medida,
                COALESCE(SUM(msd.cantidad), 1) AS cantidad
            FROM rogedibd.movimientos_stock_detalle msd
                INNER JOIN rogedibd.materiales m ON msd.material_id = m.id
                LEFT JOIN rogedibd.materiales_detalle md ON msd.material_detalle_id = md.id
                LEFT JOIN rogedibd.unidades_medida um ON m.id_unidad_medida = um.id
            WHERE msd.movimiento_stock_id = ?
            GROUP BY m.id, m.codigo, m.descripcion, um.abreviacion
        """;

        List<DespachoDetalleDTO> detalles = new ArrayList<>();

        try {
            detalles = jdbcTemplate.query(sqlDetalles, (rs, rowNum) ->
                            DespachoDetalleDTO.builder()
                                .numero(rs.getInt("numero"))
                                .codigoSap(rs.getString("codigo_sap"))
                                .descripcion(rs.getString("descripcion"))
                                .series(rs.getString("series"))
                                .unidadMedida(rs.getString("unidad_medida"))
                                .cantidad(rs.getInt("cantidad"))
                                .build(),
                    movimientoId
            );
        } catch (Exception e) {
            log.warn("No se encontraron detalles en movimientos_stock_detalle, usando datos del movimiento principal");

            // Fallback: usar datos del movimiento principal
            String sqlDetalleFallback = """
                SELECT 
                    1 AS numero,
                    m.codigo AS codigo_sap,
                    m.descripcion,
                    md.numero_serie AS series,
                    um.abreviacion AS unidad_medida,
                    1 AS cantidad
                FROM rogedibd.movimientos_stock ms
                    INNER JOIN rogedibd.materiales m ON ms.material_id = m.id
                    LEFT JOIN rogedibd.materiales_detalle md ON ms.material_detalle_id = md.id
                    LEFT JOIN rogedibd.unidades_medida um ON m.id_unidad_medida = um.id
                WHERE ms.id = ?
            """;

            detalles = jdbcTemplate.query(sqlDetalleFallback, (rs, rowNum) ->
                            DespachoDetalleDTO.builder()
                                .numero(rs.getInt("numero"))
                                .codigoSap(rs.getString("codigo_sap"))
                                .descripcion(rs.getString("descripcion"))
                                .series(rs.getString("series"))
                                .unidadMedida(rs.getString("unidad_medida"))
                                .cantidad(rs.getInt("cantidad"))
                                .build(),
                    movimientoId
            );
        }
        // Manejo seguro de la fecha
        LocalDate fechaMovimiento;
        Object fechaObj = movimiento.get("fec_movimiento");

        if (fechaObj != null) {
            if (fechaObj instanceof java.sql.Date) {
                fechaMovimiento = ((java.sql.Date) fechaObj).toLocalDate();
            } else if (fechaObj instanceof java.sql.Timestamp) {
                fechaMovimiento = ((java.sql.Timestamp) fechaObj).toLocalDateTime().toLocalDate();
            } else if (fechaObj instanceof java.util.Date) {
                fechaMovimiento = ((java.util.Date) fechaObj).toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();
            } else if (fechaObj instanceof String) {
                fechaMovimiento = java.time.LocalDate.parse((String) fechaObj);
            } else {
                log.warn("Tipo de fecha no reconocido: {}, usando fecha actual", fechaObj.getClass());
                fechaMovimiento = LocalDate.now();
            }
        } else {
            fechaMovimiento = LocalDate.now();
        }

        // Construir el DTO
        return DespachoDTO.builder()
                .empresa(movimiento.get("empresa") != null ? movimiento.get("empresa").toString() : "ROGEDI NETWORX SAC")
                .ruc(movimiento.get("ruc") != null ? movimiento.get("ruc").toString() : "undefined")
                .tipoDocumento("DESPACHO")
                .numeroDocumento(movimiento.get("num_documento") != null ?
                        movimiento.get("num_documento").toString() : "")
                .fecha(fechaMovimiento)
                .titulo(movimiento.get("nombre_tipo_movimiento").toString()+"\n"+movimiento.get("nombre_condicion").toString())
                .dni(movimiento.get("tecnico_dni") != null ? movimiento.get("tecnico_dni").toString() : "")
                .ciudad(movimiento.get("ciudad") != null ? movimiento.get("ciudad").toString() : "")
                .numeroMovimiento(movimientoId.intValue())
                .nombreTecnico(movimiento.get("tecnico_nombre") != null ?
                        movimiento.get("tecnico_nombre").toString() : "")
                .detalles(detalles)
                .tecnicoResponsable("")
                .almacen("")
                .generadoPor("Sistema")

                .build();
    }
}
