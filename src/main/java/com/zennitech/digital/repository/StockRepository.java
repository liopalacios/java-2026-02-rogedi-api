package com.zennitech.digital.repository;

import com.zennitech.digital.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class StockRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * Listar stock general con filtros y paginación
     */
    public List<StockGeneralModel> listarStockGeneral(
            String contratista,
            String condicion,
            String zonal,
            String sap,
            String articulo,
            int page,
            int size) {

        try {
            StringBuilder sql = new StringBuilder("""
                SELECT 
                    m.id as material_id,
                    m.codigo as sap,
                    m.descripcion as articulo,
                    z.codigo as material_zonal,
                    um.abreviacion as unidad_medida,
                    c.razon_social as contratista,
                    c.id as contratista_id,
                    COALESCE(agg.condicion, 'NUEVO') as condicion,
                    COALESCE(agg.zonal, 0 ) as zonal,
                    COALESCE(agg.stock_disponible, 0) as stock_disponible,
                    COALESCE(agg.stock_en_uso, 0) as stock_en_uso,
                    COALESCE(agg.stock_mantenimiento, 0) as stock_mantenimiento,
                    COALESCE(agg.tiene_series, 0) as tiene_series,
                    COALESCE(agg.stock_total, 0) as stock_total,
                    CASE WHEN agg.tiene_series > 0 THEN 'S' ELSE '' END as marca_series,
                    0 as en_transito
                FROM rogedibd.materiales m
                LEFT JOIN rogedibd.unidades_medida um ON m.id_unidad_medida = um.id
                LEFT JOIN rogedibd.contratistas c ON m.id_contratista = c.id
                LEFT JOIN rogedibd.zonales z ON m.id_zonal = z.id
                LEFT JOIN (
                    SELECT 
                        material_id,
                        condicion,
                        id_zonal as zonal,
                        COUNT(CASE WHEN estado = 'Disponible' THEN 1 END) as stock_disponible,
                        COUNT(CASE WHEN estado = 'En Uso' THEN 1 END) as stock_en_uso,
                        COUNT(CASE WHEN estado = 'Mantenimiento' THEN 1 END) as stock_mantenimiento,
                        COUNT(CASE WHEN numero_serie IS NOT NULL THEN 1 END) as tiene_series,
                        COUNT(*) as stock_total
                    FROM rogedibd.materiales_detalle
                    GROUP BY material_id, condicion, id_zonal
                ) agg ON m.id = agg.material_id
                WHERE 1=1
            """);

            List<Object> params = new ArrayList<>();

            // Filtros
            if (contratista != null && !contratista.isEmpty()) {
                sql.append(" AND UPPER(c.razon_social) LIKE UPPER(?)");
                params.add("%" + contratista + "%");
            }

            if (condicion != null && !condicion.isEmpty()) {
                sql.append(" AND UPPER(agg.condicion) = UPPER(?)");
                params.add(condicion);
            }

            if (zonal != null && !zonal.isEmpty()) {
                sql.append(" AND UPPER(agg.zonal) = UPPER(?)");
                params.add(zonal);
            }

            if (sap != null && !sap.isEmpty()) {
                sql.append(" AND UPPER(m.codigo) LIKE UPPER(?)");
                params.add("%" + sap + "%");
            }

            if (articulo != null && !articulo.isEmpty()) {
                sql.append(" AND UPPER(m.descripcion) LIKE UPPER(?)");
                params.add("%" + articulo + "%");
            }

            sql.append(" ORDER BY m.codigo NULLS LAST, m.descripcion");
            sql.append(" LIMIT ? OFFSET ?");

            params.add(size);
            params.add(page * size);

            log.debug("SQL Stock General: {}", sql);
            log.debug("Params: {}", params);

            return jdbcTemplate.query(sql.toString(), params.toArray(), new StockGeneralRowMapper());

        } catch (Exception e) {
            log.error("Error al listar stock general", e);
            return new ArrayList<>();
        }
    }

    /**
     * Contar total de registros con filtros
     */
    public int contarStockGeneral(
            String contratista,
            String condicion,
            String zonal,
            String sap,
            String articulo) {

        try {
            StringBuilder sql = new StringBuilder("""
                SELECT COUNT(DISTINCT m.id)
                FROM rogedibd.materiales m
                LEFT JOIN rogedibd.unidades_medida um ON m.id_unidad_medida = um.id
                LEFT JOIN rogedibd.contratistas c ON m.id_contratista = c.id
                LEFT JOIN rogedibd.materiales_detalle md ON m.id = md.material_id
                WHERE 1=1
            """);

            List<Object> params = new ArrayList<>();

            if (contratista != null && !contratista.isEmpty()) {
                sql.append(" AND UPPER(c.razon_social) LIKE UPPER(?)");
                params.add("%" + contratista + "%");
            }

            if (condicion != null && !condicion.isEmpty()) {
                sql.append(" AND UPPER(md.condicion) = UPPER(?)");
                params.add(condicion);
            }

            if (zonal != null && !zonal.isEmpty()) {
                sql.append(" AND UPPER(md.zonal) = UPPER(?)");
                params.add(zonal);
            }

            if (sap != null && !sap.isEmpty()) {
                sql.append(" AND UPPER(m.codigo) LIKE UPPER(?)");
                params.add("%" + sap + "%");
            }

            if (articulo != null && !articulo.isEmpty()) {
                sql.append(" AND UPPER(m.descripcion) LIKE UPPER(?)");
                params.add("%" + articulo + "%");
            }

            Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
            return count != null ? count : 0;

        } catch (Exception e) {
            log.error("Error al contar stock general", e);
            return 0;
        }
    }

    /**
     * Listar contratistas activos
     */
    public List<ContratistaModel> listarContratistas() {
        String sql = """
            SELECT id, razon_social, ruc, email
            FROM rogedibd.contratistas
            WHERE activo = true
            ORDER BY razon_social
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ContratistaModel c = new ContratistaModel();
            c.setId(rs.getInt("id"));
            c.setRazonSocial(rs.getString("razon_social"));
            c.setRuc(rs.getString("ruc"));
            c.setCodigo(rs.getString("email"));
            return c;
        });
    }

    /**
     * Listar zonales activos
     */
    public List<ZonalModel> listarZonales() {
        String sql = """
            SELECT id, codigo, nombre, region
            FROM rogedibd.zonales
            WHERE activo = true
            ORDER BY nombre
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ZonalModel z = new ZonalModel();
            z.setId(rs.getInt("id"));
            z.setCodigo(rs.getString("codigo"));
            z.setNombre(rs.getString("nombre"));
            z.setRegion(rs.getString("region"));
            return z;
        });
    }

    public List<StockSerieModel> listarStockSeries(
            String contratista,
            String condicion,
            String zonal,
            String sap,
            String articulo,
            String serie,
            int page,
            int size) {

        try {
            StringBuilder sql = new StringBuilder("""
                SELECT 
                    id,
                    contratista,
                    condicion,
                    id_zonal,
                    zonalcode,
                    sap,
                    articulo,
                    unidad_medida,
                    serie,
                    estado,
                    ultimo_movimiento,
                    dias,
                    modelo,
                    
                    fecha_compra,
                    ubicacion
                    
                FROM rogedibd.v_stock_series
                WHERE 1=1
            """);

            List<Object> params = new ArrayList<>();

            // Filtros
            if (contratista != null && !contratista.isEmpty()) {
                sql.append(" AND UPPER(contratista) LIKE UPPER(?)");
                params.add("%" + contratista + "%");
            }

            if (condicion != null && !condicion.isEmpty()) {
                sql.append(" AND UPPER(condicion) = UPPER(?)");
                params.add(condicion);
            }

            if (zonal != null && !zonal.isEmpty()) {
                sql.append(" AND id_zonal = ?");
                params.add(Integer.parseInt(zonal));
            }

            if (sap != null && !sap.isEmpty()) {
                sql.append(" AND UPPER(sap) LIKE UPPER(?)");
                params.add("%" + sap + "%");
            }

            if (articulo != null && !articulo.isEmpty()) {
                sql.append(" AND UPPER(articulo) LIKE UPPER(?)");
                params.add("%" + articulo + "%");
            }

            if (serie != null && !serie.isEmpty()) {
                sql.append(" AND UPPER(serie) LIKE UPPER(?)");
                params.add("%" + serie + "%");
            }

            sql.append(" ORDER BY id DESC");
            sql.append(" LIMIT ? OFFSET ?");

            params.add(size);
            params.add(page * size);

            log.debug("SQL Stock Series: {}", sql);
            log.debug("Params: {}", params);

            return jdbcTemplate.query(sql.toString(), params.toArray(), new StockSerieRowMapper());

        } catch (Exception e) {
            log.error("Error al listar stock series", e);
            return new ArrayList<>();
        }
    }

    public List<StockAtendidoModel> listarStockAtendidos(
            String contratista, String condicion, String zonal,
            String sap, String articulo, String serie,
            String tecnico, String tipoMovimiento, String fechaDesde,
            String fechaHasta, int page, int size) {
        try{
            StringBuilder sql = new StringBuilder("SELECT * FROM rogedibd.v_stock_atendidos WHERE 1=1");
            List<Object> params = new ArrayList<>();
            if (contratista != null && !contratista.isEmpty()) {
                sql.append(" AND UPPER(contratista) LIKE UPPER(?)");
                params.add("%" + contratista + "%");
            }
            if (condicion != null && !condicion.isEmpty()) {
                sql.append(" AND condicion = ?");
                params.add(condicion);
            }
            if (zonal != null && !zonal.isEmpty()) {
                sql.append(" AND cod_zonal = ?");
                params.add(zonal);
            }
            if (sap != null && !sap.isEmpty()) {
                sql.append(" AND sap LIKE ?");
                params.add("%" + sap + "%");
            }
            if (articulo != null && !articulo.isEmpty()) {
                sql.append(" AND UPPER(articulo) LIKE UPPER(?)");
                params.add("%" + articulo + "%");
            }
            if (serie != null && !serie.isEmpty()) {
                sql.append(" AND UPPER(serie) LIKE UPPER(?)");
                params.add("%" + serie + "%");
            }
            if (tecnico != null && !tecnico.isEmpty()) {
                sql.append(" AND (UPPER(tecnico_nombre) LIKE UPPER(?) OR UPPER(tecnico_apellido) LIKE UPPER(?) OR UPPER(tecnico_codigo) LIKE UPPER(?))");
                String tecnicoPattern = "%" + tecnico + "%";
                params.add(tecnicoPattern);
                params.add(tecnicoPattern);
                params.add(tecnicoPattern);
            }
            if (tipoMovimiento != null && !tipoMovimiento.isEmpty()) {
                sql.append(" AND tipo_movimiento = ?");
                params.add(tipoMovimiento);
            }
            if (fechaDesde != null && !fechaDesde.isEmpty()) {
                sql.append(" AND fec_movimiento >= ?::date");
                params.add(fechaDesde);
            }
            if (fechaHasta != null && !fechaHasta.isEmpty()) {
                sql.append(" AND fec_movimiento <= ?::date");
                params.add(fechaHasta);
            }


            // Paginación
            sql.append(" ORDER BY fec_movimiento DESC, movimiento_id DESC LIMIT ? OFFSET ?");
            params.add(size);
            params.add(page * size);

            return jdbcTemplate.query(sql.toString(), params.toArray(), new StockAtendidoRowMapper());

        }catch (Exception e){
            log.error("Error al listar stock series", e);
            return new ArrayList<>();
        }

    }

    public List<StockAtendidoModel> listarMovimientosStock(
            List<Integer> tipoMovimientoIds, // Nuevo parámetro: lista de IDs de tipos de movimiento
            String contratista, String condicion, String zonal,
            String sap, String articulo, String serie,
            String tecnico, String tipoMovimiento, String fechaDesde,
            String fechaHasta, int page, int size) {

        try {
            // Usar la vista general en lugar de la específica
            StringBuilder sql = new StringBuilder("SELECT * FROM rogedibd.v_stock_atendidos WHERE 1=1");
            List<Object> params = new ArrayList<>();

            // 1. Filtrar por lista de IDs de tipo de movimiento
            if (tipoMovimientoIds != null && !tipoMovimientoIds.isEmpty()) {
                sql.append(" AND tipo_movimiento_id IN (");

                // Crear placeholders para cada ID: ?, ?, ?
                String placeholders = tipoMovimientoIds.stream()
                        .map(id -> "?")
                        .collect(Collectors.joining(", "));
                sql.append(placeholders).append(")");

                // Agregar los IDs como parámetros
                params.addAll(tipoMovimientoIds);
            } else {
                // Si no se especifica, usar valores por defecto (atendidos)
                sql.append(" AND tipo_movimiento_id IN (1, 2, 5)");
            }

            // 2. Resto de filtros (mantener igual)
            if (contratista != null && !contratista.isEmpty()) {
                sql.append(" AND UPPER(contratista) LIKE UPPER(?)");
                params.add("%" + contratista + "%");
            }
            if (condicion != null && !condicion.isEmpty()) {
                sql.append(" AND condicion = ?");
                params.add(condicion);
            }
            if (zonal != null && !zonal.isEmpty()) {
                sql.append(" AND cod_zonal = ?");
                params.add(zonal);
            }
            if (sap != null && !sap.isEmpty()) {
                sql.append(" AND sap LIKE ?");
                params.add("%" + sap + "%");
            }
            if (articulo != null && !articulo.isEmpty()) {
                sql.append(" AND UPPER(articulo) LIKE UPPER(?)");
                params.add("%" + articulo + "%");
            }
            if (serie != null && !serie.isEmpty()) {
                sql.append(" AND UPPER(serie) LIKE UPPER(?)");
                params.add("%" + serie + "%");
            }
            if (tecnico != null && !tecnico.isEmpty()) {
                sql.append(" AND (UPPER(tecnico_nombre) LIKE UPPER(?) OR UPPER(tecnico_apellido) LIKE UPPER(?) OR UPPER(tecnico_codigo) LIKE UPPER(?))");
                String tecnicoPattern = "%" + tecnico + "%";
                params.add(tecnicoPattern);
                params.add(tecnicoPattern);
                params.add(tecnicoPattern);
            }

            // 3. Filtro adicional por tipo_movimiento (nombre, no ID)
            if (tipoMovimiento != null && !tipoMovimiento.isEmpty()) {
                sql.append(" AND tipo_movimiento = ?");
                params.add(tipoMovimiento);
            }

            // 4. Filtros de fecha
            if (fechaDesde != null && !fechaDesde.isEmpty()) {
                sql.append(" AND fec_movimiento >= ?::date");
                params.add(fechaDesde);
            }
            if (fechaHasta != null && !fechaHasta.isEmpty()) {
                sql.append(" AND fec_movimiento <= ?::date");
                params.add(fechaHasta);
            }

            // 5. Paginación
            sql.append(" ORDER BY fec_movimiento DESC, movimiento_id DESC LIMIT ? OFFSET ?");
            params.add(size);
            params.add(page * size);

            // Log para debugging
            log.debug("SQL generado: {}", sql.toString());
            log.debug("Parámetros: {}", params);

            return jdbcTemplate.query(sql.toString(), params.toArray(), new StockAtendidoRowMapper());

        } catch (Exception e) {
            log.error("Error al listar movimientos de stock", e);
            return new ArrayList<>();
        }
    }
    /**
     * Contar total de series con filtros
     */
    public int contarStockSeries(
            String contratista,
            String condicion,
            String zonal,
            String sap,
            String articulo,
            String serie) {

        try {
            StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*)
                FROM rogedibd.v_stock_series
                WHERE 1=1
            """);

            List<Object> params = new ArrayList<>();

            if (contratista != null && !contratista.isEmpty()) {
                sql.append(" AND UPPER(contratista) LIKE UPPER(?)");
                params.add("%" + contratista + "%");
            }

            if (condicion != null && !condicion.isEmpty()) {
                sql.append(" AND UPPER(condicion) = UPPER(?)");
                params.add(condicion);
            }

            if (zonal != null && !zonal.isEmpty()) {
                sql.append(" AND id_zonal = ?");
                params.add(Integer.parseInt(zonal));
            }

            if (sap != null && !sap.isEmpty()) {
                sql.append(" AND UPPER(sap) LIKE UPPER(?)");
                params.add("%" + sap + "%");
            }

            if (articulo != null && !articulo.isEmpty()) {
                sql.append(" AND UPPER(articulo) LIKE UPPER(?)");
                params.add("%" + articulo + "%");
            }

            if (serie != null && !serie.isEmpty()) {
                sql.append(" AND UPPER(serie) LIKE UPPER(?)");
                params.add("%" + serie + "%");
            }

            Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
            return count != null ? count : 0;

        } catch (Exception e) {
            log.error("Error al contar stock series", e);
            return 0;
        }
    }

    /* ================== ROW MAPPERS ================== */

    private static class StockSerieRowMapper implements RowMapper<StockSerieModel> {
        @Override
        public StockSerieModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            StockSerieModel serie = new StockSerieModel();
            serie.setId(rs.getLong("id"));
            serie.setContratista(rs.getString("contratista"));
            serie.setCondicion(rs.getString("condicion"));
            serie.setIdZonal(rs.getInt("id_zonal"));
            serie.setSap(rs.getString("sap"));
            serie.setArticulo(rs.getString("articulo"));
            serie.setUnidadMedida(rs.getString("unidad_medida"));
            serie.setSerie(rs.getString("serie"));
            serie.setEstado(rs.getString("estado"));
            serie.setUltimoMovimiento(rs.getString("ultimo_movimiento"));
            serie.setDias(rs.getInt("dias"));
            serie.setModelo(rs.getString("modelo"));
            serie.setZonal(rs.getString("zonalcode"));
            // Manejo de fecha
            java.sql.Date fechaCompra = rs.getDate("fecha_compra");
            if (fechaCompra != null) {
                serie.setFechaCompra(fechaCompra.toLocalDate());
            }

            serie.setUbicacion(rs.getString("ubicacion"));

            return serie;
        }
    }

    private static class StockGeneralRowMapper implements RowMapper<StockGeneralModel> {
        @Override
        public StockGeneralModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            StockGeneralModel stock = new StockGeneralModel();
            stock.setMaterialId(rs.getLong("material_id"));
            stock.setSap(rs.getString("sap"));
            stock.setArticulo(rs.getString("articulo"));
            stock.setUnidadMedida(rs.getString("unidad_medida"));
            stock.setContratista(rs.getString("contratista"));
            stock.setContratistaId(rs.getLong("contratista_id"));
            stock.setCondicion(rs.getString("condicion"));
            stock.setZonal(rs.getString("material_zonal"));
            stock.setStockDisponible(rs.getInt("stock_disponible"));
            stock.setStockEnUso(rs.getInt("stock_en_uso"));
            stock.setStockMantenimiento(rs.getInt("stock_mantenimiento"));
            stock.setTieneSeries(rs.getInt("tiene_series"));
            stock.setStockTotal(rs.getInt("stock_total"));
            stock.setMarcaSeries(rs.getString("marca_series"));
            stock.setEnTransito(rs.getInt("en_transito"));
            return stock;
        }
    }
    private static class StockAtendidoRowMapper implements RowMapper<StockAtendidoModel> {
        @Override
        public StockAtendidoModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            StockAtendidoModel model = new StockAtendidoModel();
            model.setMovimientoId(rs.getLong("movimiento_id"));
            model.setNroContratista(rs.getString("nro_contratista"));
            model.setCodZonal(rs.getString("cod_zonal"));
            model.setTipoMovimiento(rs.getString("tipo_movimiento_nombre"));
            model.setTipoMovimientoId(rs.getLong("tipo_movimiento_id"));
            model.setCondicion(rs.getString("nombre_condicion"));
            model.setTipoDocumento(rs.getString("tipo_documento"));
            model.setNumDocumento(rs.getString("num_documento"));
            model.setFecMovimiento(rs.getObject("fec_movimiento", LocalDate.class));
            model.setNroDocref(rs.getString("nro_docref"));
            model.setNroDestinoref(rs.getString("nro_destinoref"));
            model.setGlosa(rs.getString("glosa"));
            model.setUsuarioRegistro(rs.getString("creado_por"));
            //model.setFechaReg(rs.getObject("fecha_reg", LocalDateTime.class));

            // Información adicional
            model.setSap(rs.getString("sap"));
            model.setArticulo(rs.getString("articulo"));
            model.setUnidadMedida(rs.getString("unidad_medida"));
            model.setSerie(rs.getString("serie"));
            model.setContratista(rs.getString("contratista"));
            model.setZonalNombre(rs.getString("zonal_nombre"));

            // Información del técnico
            model.setTecnicoNombre(rs.getString("tecnico_nombre"));
            model.setTecnicoApellido(rs.getString("tecnico_apellido"));
            model.setTecnicoCodigo(rs.getString("tecnico_codigo"));
            model.setTecnicoNumeroDocumento(rs.getString("tecnico_documento"));

            model.setFecMovimiento(LocalDate.parse(rs.getString("fec_movimiento"), formatter ));

            model.setObservacion(rs.getString("glosa"));
            // Datos calculados
            model.setCantidad(rs.getInt("cantidad"));
            model.setDiasAtendido(rs.getInt("dias_atendido"));
            model.setEstadoMaterial(rs.getString("estado_material"));
            model.setUsuarioId(rs.getInt("usuario_id"));
            model.setCreadoEn(rs.getTimestamp("creado_en") != null ?
                    rs.getTimestamp("creado_en").toLocalDateTime() : null);
            return model;
        }
    }
}
