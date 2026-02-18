package com.zennitech.digital.repository;

import com.zennitech.digital.model.LiquidacionModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LiquidacionRepository {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Listar liquidaciones con filtros y paginación
     */
    public List<LiquidacionModel> listarLiquidaciones(
            String coLiquidacion,
            String dniCliente,
            String nombreCliente,
            String tipoInstalacionCodigo,
            String estadoRevisionCodigo,
            String tipoPropiedadCodigo,
            String fechaDesde,
            String fechaHasta,
            int page,
            int size) {

        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM rogedibd.v_liquidaciones WHERE 1=1");
            List<Object> params = new ArrayList<>();

            // Filtros
            if (coLiquidacion != null && !coLiquidacion.isEmpty()) {
                sql.append(" AND co_liquidacion LIKE ?");
                params.add("%" + coLiquidacion + "%");
            }

            if (dniCliente != null && !dniCliente.isEmpty()) {
                sql.append(" AND dni_cliente LIKE ?");
                params.add("%" + dniCliente + "%");
            }

            if (nombreCliente != null && !nombreCliente.isEmpty()) {
                sql.append(" AND UPPER(nombre_cliente) LIKE UPPER(?)");
                params.add("%" + nombreCliente + "%");
            }

            // Filtrar por códigos en lugar de IDs
            if (tipoInstalacionCodigo != null && !tipoInstalacionCodigo.isEmpty()) {
                sql.append(" AND tipo_instalacion_codigo = ?");
                params.add(tipoInstalacionCodigo);
            }

            if (estadoRevisionCodigo != null && !estadoRevisionCodigo.isEmpty()) {
                sql.append(" AND estado_revision_codigo = ?");
                params.add(estadoRevisionCodigo);
            }

            if (tipoPropiedadCodigo != null && !tipoPropiedadCodigo.isEmpty()) {
                sql.append(" AND tipo_propiedad_codigo = ?");
                params.add(tipoPropiedadCodigo);
            }

            if (fechaDesde != null && !fechaDesde.isEmpty()) {
                sql.append(" AND DATE(fecha_instalacion) >= ?::date");
                params.add(fechaDesde);
            }

            if (fechaHasta != null && !fechaHasta.isEmpty()) {
                sql.append(" AND DATE(fecha_instalacion) <= ?::date");
                params.add(fechaHasta);
            }

            // Paginación
            sql.append(" ORDER BY fecha_instalacion DESC, nro ASC");
            sql.append(" LIMIT ? OFFSET ?");
            params.add(size);
            params.add(page * size);

            log.debug("SQL Liquidaciones: {}", sql);
            log.debug("Params: {}", params);

            return jdbcTemplate.query(sql.toString(), params.toArray(), new LiquidacionRowMapper());

        } catch (Exception e) {
            log.error("Error al listar liquidaciones", e);
            return new ArrayList<>();
        }
    }

    /**
     * Contar total de liquidaciones con filtros
     */
    public int contarLiquidaciones(
            String coLiquidacion,
            String dniCliente,
            String nombreCliente,
            String tipoInstalacionCodigo,
            String estadoRevisionCodigo,
            String tipoPropiedadCodigo,
            String fechaDesde,
            String fechaHasta) {

        try {
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM rogedibd.v_liquidaciones WHERE 1=1");
            List<Object> params = new ArrayList<>();

            // Filtros (mismos que en listar)
            if (coLiquidacion != null && !coLiquidacion.isEmpty()) {
                sql.append(" AND co_liquidacion LIKE ?");
                params.add("%" + coLiquidacion + "%");
            }

            if (dniCliente != null && !dniCliente.isEmpty()) {
                sql.append(" AND dni_cliente LIKE ?");
                params.add("%" + dniCliente + "%");
            }

            if (nombreCliente != null && !nombreCliente.isEmpty()) {
                sql.append(" AND UPPER(nombre_cliente) LIKE UPPER(?)");
                params.add("%" + nombreCliente + "%");
            }

            if (tipoInstalacionCodigo != null && !tipoInstalacionCodigo.isEmpty()) {
                sql.append(" AND tipo_instalacion_codigo = ?");
                params.add(tipoInstalacionCodigo);
            }

            if (estadoRevisionCodigo != null && !estadoRevisionCodigo.isEmpty()) {
                sql.append(" AND estado_revision_codigo = ?");
                params.add(estadoRevisionCodigo);
            }

            if (tipoPropiedadCodigo != null && !tipoPropiedadCodigo.isEmpty()) {
                sql.append(" AND tipo_propiedad_codigo = ?");
                params.add(tipoPropiedadCodigo);
            }

            if (fechaDesde != null && !fechaDesde.isEmpty()) {
                sql.append(" AND DATE(fecha_instalacion) >= ?::date");
                params.add(fechaDesde);
            }

            if (fechaHasta != null && !fechaHasta.isEmpty()) {
                sql.append(" AND DATE(fecha_instalacion) <= ?::date");
                params.add(fechaHasta);
            }

            Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
            return count != null ? count : 0;

        } catch (Exception e) {
            log.error("Error al contar liquidaciones", e);
            return 0;
        }
    }

    /**
     * Obtener IDs por códigos para inserción
     */
    public Integer obtenerTipoInstalacionIdPorCodigo(String codigo) {
        try {
            String sql = "SELECT id FROM rogedibd.tipos_instalacion WHERE codigo = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, codigo);
        } catch (Exception e) {
            log.warn("No se encontró tipo instalación con código: {}", codigo);
            return null;
        }
    }

    public Integer obtenerEstadoRevisionIdPorCodigo(String codigo) {
        try {
            String sql = "SELECT id FROM rogedibd.estados_revision WHERE codigo = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, codigo);
        } catch (Exception e) {
            log.warn("No se encontró estado revisión con código: {}", codigo);
            return null;
        }
    }

    public Integer obtenerTipoPropiedadIdPorCodigo(String codigo) {
        try {
            String sql = "SELECT id FROM rogedibd.tipos_propiedad WHERE codigo = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, codigo);
        } catch (Exception e) {
            log.warn("No se encontró tipo propiedad con código: {}", codigo);
            return null;
        }
    }

    /**
     * Insertar o actualizar liquidación
     */
    public boolean guardarLiquidacion(LiquidacionModel liquidacion) {
        try {
            // Obtener IDs de las tablas relacionadas
            Integer tipoPropiedadId = obtenerTipoPropiedadIdPorCodigo(liquidacion.getTipoPropiedadCodigo());
            Integer tipoInstalacionId = obtenerTipoInstalacionIdPorCodigo(liquidacion.getTipoInstalacionCodigo());
            Integer estadoRevisionId = obtenerEstadoRevisionIdPorCodigo(liquidacion.getEstadoRevisionCodigo());

            String sql = """
                INSERT INTO rogedibd.liquidaciones (
                    co_liquidacion, numero_secuencia, fecha_instalacion, 
                    tipo_partida, tipo_propiedad_id, tipo_instalacion_id, 
                    estado_revision_id, numero_acta, codigo_pedido, 
                    dni_cliente, nombre_cliente, direccion, 
                    torre_departamento, nombre_condominio, paquete_servicio,
                    metraje, cantidad_mesh, rotulado_cto_nap,
                    observacion_contrata, observacion_operador,
                    creado_por, estado
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVO')
                ON CONFLICT (co_liquidacion) DO UPDATE SET
                    numero_secuencia = EXCLUDED.numero_secuencia,
                    fecha_instalacion = EXCLUDED.fecha_instalacion,
                    tipo_partida = EXCLUDED.tipo_partida,
                    tipo_propiedad_id = EXCLUDED.tipo_propiedad_id,
                    tipo_instalacion_id = EXCLUDED.tipo_instalacion_id,
                    estado_revision_id = EXCLUDED.estado_revision_id,
                    numero_acta = EXCLUDED.numero_acta,
                    codigo_pedido = EXCLUDED.codigo_pedido,
                    dni_cliente = EXCLUDED.dni_cliente,
                    nombre_cliente = EXCLUDED.nombre_cliente,
                    direccion = EXCLUDED.direccion,
                    torre_departamento = EXCLUDED.torre_departamento,
                    nombre_condominio = EXCLUDED.nombre_condominio,
                    paquete_servicio = EXCLUDED.paquete_servicio,
                    metraje = EXCLUDED.metraje,
                    cantidad_mesh = EXCLUDED.cantidad_mesh,
                    rotulado_cto_nap = EXCLUDED.rotulado_cto_nap,
                    observacion_contrata = EXCLUDED.observacion_contrata,
                    observacion_operador = EXCLUDED.observacion_operador,
                    actualizado_en = CURRENT_TIMESTAMP,
                    actualizado_por = EXCLUDED.creado_por
                RETURNING id
            """;

            Integer id = jdbcTemplate.queryForObject(sql, Integer.class,
                    liquidacion.getCoLiquidacion(),
                    liquidacion.getNumeroSecuencia(),
                    liquidacion.getFechaInstalacion() != null ?
                            Timestamp.valueOf(liquidacion.getFechaInstalacion()) : null,
                    liquidacion.getTipoPartida(),
                    tipoPropiedadId,
                    tipoInstalacionId,
                    estadoRevisionId,
                    liquidacion.getNumeroActa(),
                    liquidacion.getCodigoPedido(),
                    liquidacion.getDniCliente(),
                    liquidacion.getNombreCliente(),
                    liquidacion.getDireccion(),
                    liquidacion.getTorreDepartamento(),
                    liquidacion.getNombreCondominio(),
                    liquidacion.getPaqueteServicio(),
                    liquidacion.getMetraje(),
                    liquidacion.getCantidadMesh(),
                    liquidacion.getRotuladoCtoNap(),
                    liquidacion.getObservacionContrata(),
                    liquidacion.getObservacionOperador(),
                    liquidacion.getCreadoPor()
            );

            return id != null;

        } catch (Exception e) {
            log.error("Error al guardar liquidación", e);
            return false;
        }
    }

    /**
     * Importar liquidaciones desde Excel (batch insert)
     */
    public int importarLiquidacionesDesdeExcel(List<LiquidacionModel> liquidaciones) {
        try {
            String sql = """
                INSERT INTO rogedibd.liquidaciones (
                    co_liquidacion, numero_secuencia, fecha_instalacion, 
                    tipo_partida, tipo_propiedad_id, tipo_instalacion_id, 
                    estado_revision_id, numero_acta, codigo_pedido, 
                    dni_cliente, nombre_cliente, direccion, 
                    torre_departamento, nombre_condominio, paquete_servicio,
                    metraje, cantidad_mesh, rotulado_cto_nap,
                    observacion_contrata, observacion_operador,
                    creado_por, estado
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVO')
                ON CONFLICT (co_liquidacion) DO UPDATE SET
                    numero_secuencia = EXCLUDED.numero_secuencia,
                    fecha_instalacion = EXCLUDED.fecha_instalacion,
                    tipo_partida = EXCLUDED.tipo_partida,
                    tipo_propiedad_id = EXCLUDED.tipo_propiedad_id,
                    tipo_instalacion_id = EXCLUDED.tipo_instalacion_id,
                    estado_revision_id = EXCLUDED.estado_revision_id,
                    numero_acta = EXCLUDED.numero_acta,
                    codigo_pedido = EXCLUDED.codigo_pedido,
                    dni_cliente = EXCLUDED.dni_cliente,
                    nombre_cliente = EXCLUDED.nombre_cliente,
                    direccion = EXCLUDED.direccion,
                    torre_departamento = EXCLUDED.torre_departamento,
                    nombre_condominio = EXCLUDED.nombre_condominio,
                    paquete_servicio = EXCLUDED.paquete_servicio,
                    metraje = EXCLUDED.metraje,
                    cantidad_mesh = EXCLUDED.cantidad_mesh,
                    rotulado_cto_nap = EXCLUDED.rotulado_cto_nap,
                    observacion_contrata = EXCLUDED.observacion_contrata,
                    observacion_operador = EXCLUDED.observacion_operador,
                    actualizado_en = CURRENT_TIMESTAMP,
                    actualizado_por = EXCLUDED.creado_por
            """;

            List<Object[]> batchArgs = new ArrayList<>();
            for (LiquidacionModel liquidacion : liquidaciones) {
                // Obtener IDs de las tablas relacionadas
                Integer tipoPropiedadId = obtenerTipoPropiedadIdPorCodigo(liquidacion.getTipoPropiedadCodigo());
                Integer tipoInstalacionId = obtenerTipoInstalacionIdPorCodigo(liquidacion.getTipoInstalacionCodigo());
                Integer estadoRevisionId = obtenerEstadoRevisionIdPorCodigo(liquidacion.getEstadoRevisionCodigo());

                Object[] params = new Object[]{
                        liquidacion.getCoLiquidacion(),
                        liquidacion.getNumeroSecuencia(),
                        liquidacion.getFechaInstalacion() != null ?
                                Timestamp.valueOf(liquidacion.getFechaInstalacion()) : null,
                        liquidacion.getTipoPartida(),
                        tipoPropiedadId,
                        tipoInstalacionId,
                        estadoRevisionId,
                        liquidacion.getNumeroActa(),
                        liquidacion.getCodigoPedido(),
                        liquidacion.getDniCliente(),
                        liquidacion.getNombreCliente(),
                        liquidacion.getDireccion(),
                        liquidacion.getTorreDepartamento(),
                        liquidacion.getNombreCondominio(),
                        liquidacion.getPaqueteServicio(),
                        liquidacion.getMetraje(),
                        liquidacion.getCantidadMesh(),
                        liquidacion.getRotuladoCtoNap(),
                        liquidacion.getObservacionContrata(),
                        liquidacion.getObservacionOperador(),
                        liquidacion.getCreadoPor()
                };
                batchArgs.add(params);
            }

            int[] results = jdbcTemplate.batchUpdate(sql, batchArgs);
            return results.length;

        } catch (Exception e) {
            log.error("Error al importar liquidaciones desde Excel", e);
            return 0;
        }
    }

    /* ================== ROW MAPPER ================== */

    private static class LiquidacionRowMapper implements RowMapper<LiquidacionModel> {
        @Override
        public LiquidacionModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            LiquidacionModel liquidacion = new LiquidacionModel();

            liquidacion.setId(rs.getLong("id"));
            liquidacion.setCoLiquidacion(rs.getString("co_liquidacion"));
            liquidacion.setNumeroSecuencia(rs.getInt("nro"));

            // Fecha de instalación
            Timestamp fechaInstalacion = rs.getTimestamp("fecha_instalacion");
            if (fechaInstalacion != null) {
                liquidacion.setFechaInstalacion(fechaInstalacion.toLocalDateTime());
            }

            liquidacion.setTipoPartida(rs.getString("tipo_partida"));
            liquidacion.setTipoPropiedadCodigo(rs.getString("tipo_propiedad_codigo"));
            liquidacion.setTipoPropiedadNombre(rs.getString("tipo_propiedad_nombre"));
            liquidacion.setTipoInstalacionCodigo(rs.getString("tipo_instalacion_codigo"));
            liquidacion.setTipoInstalacionNombre(rs.getString("tipo_instalacion_nombre"));
            liquidacion.setEstadoRevisionCodigo(rs.getString("estado_revision_codigo"));
            liquidacion.setEstadoRevisionNombre(rs.getString("estado_revision_nombre"));
            liquidacion.setNumeroActa(rs.getString("numero_acta"));
            liquidacion.setCodigoPedido(rs.getString("codigo_pedido"));
            liquidacion.setDniCliente(rs.getString("dni_cliente"));
            liquidacion.setNombreCliente(rs.getString("nombre_cliente"));
            liquidacion.setDireccion(rs.getString("direccion"));
            liquidacion.setTorreDepartamento(rs.getString("torre_departamento"));
            liquidacion.setNombreCondominio(rs.getString("nombre_condominio"));
            liquidacion.setPaqueteServicio(rs.getString("paquete_servicio"));
            liquidacion.setMetraje(rs.getBigDecimal("metraje"));
            liquidacion.setCantidadMesh(rs.getInt("cantidad_mesh"));
            liquidacion.setRotuladoCtoNap(rs.getString("rotulado_cto_nap"));
            liquidacion.setObservacionContrata(rs.getString("observacion_contrata"));
            liquidacion.setObservacionOperador(rs.getString("observacion_operador"));
            liquidacion.setZonal(rs.getString("cod_zonal"));
            liquidacion.setPaqueteCodigo(rs.getString("cod_paquete"));

            // Campos de auditoría
            Timestamp creadoEn = rs.getTimestamp("creado_en");
            if (creadoEn != null) {
                liquidacion.setCreadoEn(creadoEn.toLocalDateTime());
            }

            liquidacion.setCreadoPor(rs.getString("creado_por"));
            liquidacion.setEstado(rs.getString("estado"));

            return liquidacion;
        }
    }
}
