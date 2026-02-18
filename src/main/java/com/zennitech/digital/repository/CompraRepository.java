package com.zennitech.digital.repository;

import com.zennitech.digital.model.CompraDetalleModel;
import com.zennitech.digital.model.CompraModel;
import com.zennitech.digital.model.MaterialDetalleModel;
import com.zennitech.digital.pojo.SerieInfoDTO;
import com.zennitech.digital.service.MaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CompraRepository {
    private final JdbcTemplate jdbcTemplate;

    /* ================== COMPRA ================== */

    public Long saveCompra(CompraModel compra) {
        String sql = """
            INSERT INTO rogedibd.compras
            (tipo_compra, tipo_documento, numero_documento, fecha_ingreso,
             observacion, estado, usuario_creacion, fecha_creacion)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id_compra
        """;
        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                compra.getTipoCompra(),
                compra.getTipoDocumento(),
                compra.getNumeroDocumento(),
                compra.getFechaIngreso(),
                compra.getObservacion(),
                compra.getEstado(),
                compra.getUsuarioCreacion(),
                LocalDateTime.now()
        );
    }

    /* ================== DETALLE ================== */

    public Long saveDetalle(CompraDetalleModel detalle) {
        String sql = """
        INSERT INTO rogedibd.compras_detalle
        (id_compra, material_id, codigo_material, descripcion_material,cantidad, precio_unitario, precio_total,
         usuario_creacion, fecha_creacion)
        VALUES (?, ?, ?, ?, ?, ?, ?,?,?)
        RETURNING id_compra_detalle
        """;
        System.out.println(detalle);
        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                detalle.getIdCompra(),
                detalle.getMaterialId(),
                detalle.getCodigoMaterial(),
                detalle.getDescripcion(),
                detalle.getCantidad(),
                detalle.getPrecio(),
                detalle.getPrecioTotal(),
                detalle.getUsuarioCreacion(),
                LocalDateTime.now()
        );
    }

    /* ================== SERIES ================== */
    /**
     * Verificar si una serie ya existe con estado Disponible
     */
    public boolean existeSerieDisponible(String numeroSerie) {
        try {
            String sql = """
            SELECT COUNT(*) 
            FROM rogedibd.materiales_detalle
            WHERE UPPER(numero_serie) = UPPER(?) 
            AND UPPER(estado) = 'DISPONIBLE'
        """;

            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, numeroSerie);
            return count != null && count > 0;

        } catch (Exception e) {
            log.error("Error al verificar serie: {}", numeroSerie, e);
            return false;
        }
    }
    /**
     * Obtener información de una serie existente
     */
    public SerieInfoDTO obtenerInfoSerie(String numeroSerie) {
        try {
            String sql = """
            SELECT md.numero_serie, md.estado, md.material_id,
                   m.id as codigo_material, m.descripcion as descripcion_material
            FROM rogedibd.materiales_detalle md
            LEFT JOIN rogedibd.materiales m ON md.material_id = m.id
            WHERE UPPER(md.numero_serie) = UPPER(?)
        """;

            List<SerieInfoDTO> result = jdbcTemplate.query(sql, (rs, rowNum) -> {
                SerieInfoDTO info = new SerieInfoDTO();
                info.setNumeroSerie(rs.getString("numero_serie"));
                info.setEstado(rs.getString("estado"));
                info.setMaterialId(rs.getLong("material_id"));
                info.setCodigoMaterial(rs.getString("codigo_material"));
                info.setDescripcionMaterial(rs.getString("descripcion_material"));
                return info;
            }, numeroSerie);

            return result.isEmpty() ? null : result.get(0);

        } catch (Exception e) {
            log.error("Error al obtener info de serie: {}", numeroSerie, e);
            return null;
        }
    }
    public void saveSerie(Long detalleId, Long materialId, String numeroSerie, LocalDate fechaIngreso,
                          String creadoPor) {
        String sql = """
            INSERT INTO rogedibd.materiales_detalle
            (compra_detalle_id, material_id, numero_serie,estado,fecha_compra, creado_por, creado_en)
            VALUES (?, ?, ?, ?,?, ?, ?)
            """;

        jdbcTemplate.update(sql,
                detalleId,
                materialId,
                numeroSerie,
                "Disponible",
                fechaIngreso,
                creadoPor,
                LocalDateTime.now()
        );
    }

    /**
     * Lista compras con filtros y paginación
     */
    public List<CompraModel> listarCompras(String tipoDocumento, String numeroDocumento,
                                           String estado, int page, int size) {
        StringBuilder sql = new StringBuilder("""
            SELECT id_compra, tipo_compra, tipo_documento, numero_documento, 
                   fecha_ingreso, estado, observacion,
                   usuario_creacion, fecha_creacion
            FROM rogedibd.compras
            WHERE 1=1
        """);

        if (tipoDocumento != null && !tipoDocumento.isEmpty()) {
            sql.append(" AND tipo_documento = ?");
        }
        if (numeroDocumento != null && !numeroDocumento.isEmpty()) {
            sql.append(" AND numero_documento LIKE ?");
        }
        if (estado != null && !estado.isEmpty()) {
            sql.append(" AND estado = ?");
        }

        sql.append(" ORDER BY fecha_creacion DESC");
        sql.append(" LIMIT ? OFFSET ?");

        return jdbcTemplate.query(
                sql.toString(),
                prepareStatementSetter(tipoDocumento, numeroDocumento, estado, size, page * size),
                new CompraRowMapper()
        );
    }
    /**
     * Cuenta total de compras con filtros
     */
    public int contarCompras(String tipoDocumento, String numeroDocumento, String estado) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*)
            FROM rogedibd.compras
            WHERE 1=1
        """);

        if (tipoDocumento != null && !tipoDocumento.isEmpty()) {
            sql.append(" AND tipo_documento = ?");
        }
        if (numeroDocumento != null && !numeroDocumento.isEmpty()) {
            sql.append(" AND numero_documento LIKE ?");
        }
        if (estado != null && !estado.isEmpty()) {
            sql.append(" AND estado = ?");
        }

        return jdbcTemplate.queryForObject(
                sql.toString(),
                Integer.class,
                prepareCountParams(tipoDocumento, numeroDocumento, estado)
        );
    }

    /**
     * Finalizar compra
     */
    public int finalizarCompra(Long id) {
        String sql = """
            UPDATE rogedibd.compras
            SET estado = 'F',
                actualizado_por = ?,
                actualizado_en = ?
            WHERE id_compra = ? AND estado = 'P'
        """;
        return jdbcTemplate.update(sql, "admin", LocalDateTime.now(), id);
    }

    /**
     * Buscar compra por ID
     */
    public CompraModel findById(Long id) {
        String sql = """
            SELECT id_compra, tipo_compra, tipo_documento, numero_documento, 
                   fecha_ingreso, estado, observacion,
                   usuario_creacion, fecha_creacion
            FROM rogedibd.compras
            WHERE id_compra = ?
        """;

        List<CompraModel> compras = jdbcTemplate.query(sql, new CompraRowMapper(), id);
        return compras.isEmpty() ? null : compras.get(0);
    }
    public CompraModel findByIdWithDetails(Long id) {
        try {
            String sql = """
            SELECT id_compra, tipo_compra, tipo_documento, numero_documento, 
                   fecha_ingreso, estado, observacion,
                   usuario_creacion, fecha_creacion
            FROM rogedibd.compras
            WHERE id_compra = ?
        """;

            List<CompraModel> compras = jdbcTemplate.query(sql, new CompraRowMapper(), id);

            if (compras.isEmpty()) {
                return null;
            }

            CompraModel compra = compras.get(0);

            // Cargar materiales de la compra
            compra.setMateriales(findMaterialesByCompraId(id));

            return compra;

        } catch (Exception e) {
            //log.error("Error en findByIdWithDetails con ID: {}", id, e);
            throw new RuntimeException("Error al buscar compra: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener materiales de una compra
     */
    private List<CompraDetalleModel> findMaterialesByCompraId(Long compraId) {
        try {
            String sql = """
            SELECT id_compra_detalle, id_compra, material_id, 
                   codigo_material, descripcion_material, cantidad,
                   precio_unitario, precio_total 
            FROM rogedibd.compras_detalle
            WHERE id_compra = ?
        """;

            List<CompraDetalleModel> materiales = jdbcTemplate.query(
                    sql,
                    new CompraDetalleRowMapper(),
                    compraId
            );

            // Cargar series de cada material
            for (CompraDetalleModel material : materiales) {
                material.setSeries(findSeriesByDetalleId(material.getId() ,material.getMaterialId()));
            }

            return materiales;

        } catch (Exception e) {
            //log.error("Error al buscar materiales de compra ID: {}", compraId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtener series de un material
     */
    private List<String> findSeriesByDetalleId(Long detalleId, Long materialId) {
        try {
            String sql = """
            SELECT numero_serie
            FROM rogedibd.materiales_detalle
            WHERE compra_detalle_id = ? and material_id = ?
        """;

            return jdbcTemplate.query(sql,  (rs, rowNum) ->
                    rs.getString("numero_serie"), detalleId, materialId);

        } catch (Exception e) {
            //log.error("Error al buscar series de detalle ID: {}", detalleId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Actualizar compra
     */
    public int updateCompra(Long id, CompraModel compra) {
        String sql = """
        UPDATE rogedibd.compras
        SET tipo_compra = ?,
            tipo_documento = ?,
            numero_documento = ?,
            fecha_ingreso = ?,
            observacion = ?,
            actualizado_por = ?,
            actualizado_en = ?
        WHERE id_compra = ? AND estado = 'P'
    """;

        return jdbcTemplate.update(
                sql,
                compra.getTipoCompra(),
                compra.getTipoDocumento(),
                compra.getNumeroDocumento(),
                compra.getFechaIngreso(),
                compra.getObservacion(),
                "admin", // Obtener del contexto
                LocalDateTime.now(),
                id
        );
    }

    /* ================== HELPERS ================== */

    private Object[] prepareStatementSetter(String tipoDocumento, String numeroDocumento,
                                            String estado, int size, int offset) {
        java.util.List<Object> params = new java.util.ArrayList<>();

        if (tipoDocumento != null && !tipoDocumento.isEmpty()) {
            params.add(tipoDocumento);
        }
        if (numeroDocumento != null && !numeroDocumento.isEmpty()) {
            params.add("%" + numeroDocumento + "%");
        }
        if (estado != null && !estado.isEmpty()) {
            params.add(estado);
        }

        params.add(size);
        params.add(offset);

        return params.toArray();
    }

    private Object[] prepareCountParams(String tipoDocumento, String numeroDocumento, String estado) {
        java.util.List<Object> params = new java.util.ArrayList<>();

        if (tipoDocumento != null && !tipoDocumento.isEmpty()) {
            params.add(tipoDocumento);
        }
        if (numeroDocumento != null && !numeroDocumento.isEmpty()) {
            params.add("%" + numeroDocumento + "%");
        }
        if (estado != null && !estado.isEmpty()) {
            params.add(estado);
        }

        return params.toArray();
    }

    /* ================== ROW MAPPERS ================== */

    private static class CompraDetalleRowMapper implements RowMapper<CompraDetalleModel> {
        @Override
        public CompraDetalleModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            CompraDetalleModel detalle = new CompraDetalleModel();
            detalle.setId(rs.getLong("id_compra_detalle"));
            detalle.setIdCompra(rs.getLong("id_compra"));
            detalle.setMaterialId(rs.getLong("material_id"));
            detalle.setCodigoMaterial(rs.getString("codigo_material"));
            detalle.setDescripcion(rs.getString("descripcion_material"));
            detalle.setCantidad(rs.getInt("cantidad"));
            detalle.setPrecio(rs.getDouble("precio_unitario"));
            detalle.setPrecioTotal(rs.getDouble("precio_total"));
            return detalle;
        }
    }

    private static class SerieRowMapper implements RowMapper<MaterialDetalleModel> {
        @Override
        public MaterialDetalleModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            MaterialDetalleModel serie = new MaterialDetalleModel();
            serie.setId(rs.getLong("id_material_detalle"));
            serie.setNumeroSerie(rs.getString("numero_serie"));
            serie.setModelo(rs.getString("modelo"));
            serie.setEstado(rs.getString("estado"));
            return serie;
        }
    }

    private static class CompraRowMapper implements RowMapper<CompraModel> {
        @Override
        public CompraModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            CompraModel compra = new CompraModel();
            compra.setId(rs.getLong("id_compra"));
            compra.setTipoCompra(rs.getString("tipo_compra"));
            compra.setTipoDocumento(rs.getString("tipo_documento"));
            compra.setNumeroDocumento(rs.getString("numero_documento"));
            compra.setFechaIngreso(rs.getObject("fecha_ingreso", LocalDate.class));
            compra.setEstado(rs.getString("estado"));
            compra.setObservacion(rs.getString("observacion"));
            compra.setUsuarioCreacion(rs.getString("usuario_creacion"));
            compra.setFechaCreacion(rs.getObject("fecha_creacion", LocalDateTime.class));
            return compra;
        }
    }
}
