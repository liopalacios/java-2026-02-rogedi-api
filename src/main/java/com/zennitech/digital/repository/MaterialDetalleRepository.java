package com.zennitech.digital.repository;

import com.zennitech.digital.model.MaterialDetalleModel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MaterialDetalleRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<MaterialDetalleModel> listar(String codigo, String descripcion, int page, int size) {
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM rogedibd.materiales_detalle d " +
                "inner join rogedibd.materiales m on m.id = d.material_id "
        );

        List<Object> params = new ArrayList<>();
        boolean whereAdded = false;

        if (codigo != null && !codigo.isEmpty()) {
            sql.append(whereAdded ? " AND " : " WHERE ");
            sql.append("m.codigo ILIKE ?");
            params.add("%" + codigo + "%");
            whereAdded = true;
        }

        if (descripcion != null && !descripcion.isEmpty()) {
            sql.append(whereAdded ? " AND " : " WHERE ");
            sql.append("d.numero_serie ILIKE ?");
            params.add("%" + descripcion + "%");
            whereAdded = true;
        }

        sql.append(" ORDER BY d.id OFFSET ? LIMIT ?");
        params.add(page * size);
        params.add(size);

        return jdbcTemplate.query(sql.toString(), params.toArray(), serieRowMapper);
    }
    private final RowMapper<MaterialDetalleModel> serieRowMapper = (rs, rowNum) -> {
        MaterialDetalleModel s = new MaterialDetalleModel();
        s.setId(rs.getLong("id"));
        s.setModelo(rs.getString("modelo"));
        s.setNumeroSerie(rs.getString("numero_serie"));
        s.setMaterialId(rs.getLong("material_id"));
        s.setEstado(rs.getString("estado"));
        s.setAnioFabricacion(rs.getInt("anio_fabricacion"));
        s.setFechaCompra(rs.getString("fecha_compra")!=null?
                LocalDate.parse(rs.getString("fecha_compra")):null);
        return s;
    };
    public int contar(String codigo, String descripcion) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM rogedibd.materiales_detalle d " +
                        "inner join rogedibd.materiales m on m.id = d.material_id "
        );
        List<Object> params = new ArrayList<>();
        boolean whereAdded = false;

        if (codigo != null && !codigo.isEmpty()) {
            sql.append(whereAdded ? " AND " : " WHERE ");
            sql.append("m.codigo ILIKE ?");
            params.add("%" + codigo + "%");
            whereAdded = true;
        }

        if (descripcion != null && !descripcion.isEmpty()) {
            sql.append(whereAdded ? " AND " : " WHERE ");
            sql.append("d.numero_serie ILIKE ?");
            params.add("%" + descripcion + "%");
            whereAdded = true;
        }

        return jdbcTemplate.queryForObject(sql.toString(), params.toArray(), Integer.class);
    }

    public Optional<MaterialDetalleModel> findById(Long id) {
        String sql = "SELECT * FROM rogedibd.materiales_detalle WHERE id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(MaterialDetalleModel.class), id)
                .stream()
                .findFirst();
    }

    public List<MaterialDetalleModel> findByMaterialId(Long materialId) {
        String sql = "SELECT * FROM rogedibd.materiales_detalle WHERE material_id = ? ";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(MaterialDetalleModel.class), materialId);
    }

    public int save(MaterialDetalleModel detalle) {
        String sql = """
                INSERT INTO rogedibd.materiales_detalle
                (material_id, numero_serie, modelo, anio_fabricacion, estado, fecha_compra, fecha_garantia, ubicacion, creado_por, creado_en)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(sql,
                detalle.getMaterialId(),
                detalle.getNumeroSerie(),
                detalle.getModelo(),
                detalle.getAnioFabricacion(),
                detalle.getEstado(),
                detalle.getFechaCompra(),
                detalle.getFechaGarantia(),
                detalle.getUbicacion(),
                detalle.getCreadoPor(),
                Timestamp.valueOf(detalle.getCreadoEn())
        );
    }

    public int update(Long id, MaterialDetalleModel detalle) {
        String sql = """
                UPDATE rogedibd.materiales_detalle
                SET numero_serie = ?, modelo = ?, anio_fabricacion = ?, estado = ?, fecha_compra = ?, fecha_garantia = ?, 
                    ubicacion = ?, actualizado_por = ?, actualizado_en = ?
                WHERE id = ?
                """;
        return jdbcTemplate.update(sql,
                detalle.getNumeroSerie(),
                detalle.getModelo(),
                detalle.getAnioFabricacion(),
                detalle.getEstado(),
                detalle.getFechaCompra(),
                detalle.getFechaGarantia(),
                detalle.getUbicacion(),
                detalle.getActualizadoPor(),
                Timestamp.valueOf(detalle.getActualizadoEn()),
                id
        );
    }

    public int delete(Long id) {
        String sql = "DELETE FROM rogedibd.materiales_detalle WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * Verifica si existe una serie con el número dado
     */
    public boolean existsByNumeroSerie(String numeroSerie) {
        String sql = "SELECT COUNT(*) FROM rogedibd.materiales_detalle WHERE numero_serie = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, numeroSerie);
        return count != null && count > 0;
    }

    /**
     * Guarda múltiples series de forma masiva (batch insert)
     * Más eficiente que guardar uno por uno
     */
    public int[][] saveAll(List<MaterialDetalleModel> detalles) {
        String sql = """
                INSERT INTO rogedibd.materiales_detalle
                (material_id, numero_serie, modelo, anio_fabricacion, estado, fecha_compra, fecha_garantia, ubicacion, creado_por, creado_en)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.batchUpdate(sql, detalles, detalles.size(),
                (ps, detalle) -> {
                    ps.setObject(1, detalle.getMaterialId());
                    ps.setString(2, detalle.getNumeroSerie());
                    ps.setString(3, detalle.getModelo());
                    ps.setObject(4, detalle.getAnioFabricacion());
                    ps.setString(5, detalle.getEstado() != null ? detalle.getEstado() : "DISPONIBLE");
                    ps.setObject(6, detalle.getFechaCompra());
                    ps.setObject(7, detalle.getFechaGarantia());
                    ps.setString(8, detalle.getUbicacion());
                    ps.setString(9, detalle.getCreadoPor() != null ? detalle.getCreadoPor() : "SISTEMA");
                    ps.setTimestamp(10, Timestamp.valueOf(
                            detalle.getCreadoEn() != null ? detalle.getCreadoEn() : LocalDateTime.now()
                    ));
                });
    }

}
