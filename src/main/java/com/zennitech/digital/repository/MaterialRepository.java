package com.zennitech.digital.repository;

import com.zennitech.digital.model.MaterialDetalleModel;
import com.zennitech.digital.model.MaterialModel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MaterialRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<MaterialModel> materialRowMapper = new RowMapper<>() {
        @Override
        public MaterialModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            MaterialModel m = new MaterialModel();
            m.setId(rs.getLong("id"));
            m.setCodigo(rs.getString("codigo"));
            m.setDescripcion(rs.getString("descripcion"));
            m.setSeriado(rs.getBoolean("seriado")== Boolean.parseBoolean(null) ?false:true);
            m.setCantidadSeries(rs.getInt("cantidad_series"));
            m.setCreadoPor(rs.getString("creado_por"));
            m.setCreadoEn(rs.getTimestamp("creado_en") != null ?
                    rs.getTimestamp("creado_en").toLocalDateTime() : null);
            m.setActualizadoPor(rs.getString("actualizado_por"));
            m.setActualizadoEn(rs.getTimestamp("actualizado_en") != null ?
                    rs.getTimestamp("actualizado_en").toLocalDateTime() : null);
            return m;
        }
    };

    public int contar(String codigo, String descripcion) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM rogedibd.materiales m "
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
            sql.append("m.numero_serie ILIKE ?");
            params.add("%" + descripcion + "%");
            whereAdded = true;
        }

        return jdbcTemplate.queryForObject(sql.toString(), params.toArray(), Integer.class);
    }


    // 🔹 Listar tod-os
    public List<MaterialModel> findAll() {
        String sql = "SELECT m.id,\n" +
                "           m.codigo,\n" +
                "           m.descripcion,m.seriado,\n" +
                "           COUNT(s.id) AS cantidad_series,\n" +
                "m.creado_por, m.creado_en, m.actualizado_por, m.actualizado_en " +
                "    FROM rogedibd.materiales m\n" +
                "    LEFT JOIN rogedibd.materiales_detalle s ON s.material_id = m.id\n" +
                "    GROUP BY m.id, m.codigo, m.descripcion,m.seriado\n" +
                "    ORDER BY m.creado_en desc";
        return jdbcTemplate.query(sql, materialRowMapper);
    }

    // 🔹 Buscar por id
    public Optional<MaterialModel> findById(Long id) {
        String sql = "SELECT * FROM rogedibd.materiales WHERE id = ?";
        List<MaterialModel> result = jdbcTemplate.query(sql, materialRowMapper, id);
        return result.stream().findFirst();
    }

    // 🔹 Insertar
    public int save(MaterialModel material) {
        String sql = """
                INSERT INTO rogedibd.materiales (codigo, descripcion, seriado, creado_por, creado_en)
                VALUES (?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(sql,
                material.getCodigo(),
                material.getDescripcion(),
                material.getSeriado(),
                material.getCreadoPor(),
                LocalDateTime.now()
        );
    }

    // 🔹 Actualizar
    public int update(Long id, MaterialModel material) {
        String sql = """
                UPDATE rogedibd.materiales
                SET codigo = ?, descripcion = ?, actualizado_por = ?, actualizado_en = ?
                WHERE id = ?
                """;
        return jdbcTemplate.update(sql,
                material.getCodigo(),
                material.getDescripcion(),
                material.getActualizadoPor(),
                LocalDateTime.now(),
                id
        );
    }

    // 🔹 Eliminar
    public int deleteById(Long id) {
        String sql = "UPDATE rogedibd.materiales SET activo = false WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public List<MaterialModel> listarPorCodigo(String codigo, String descripcion, int page, int size) {
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM rogedibd.materiales m "
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
            sql.append("m.numero_serie ILIKE ?");
            params.add("%" + descripcion + "%");
            whereAdded = true;
        }

        sql.append(" ORDER BY m.creado_en desc OFFSET ? LIMIT ?");
        params.add(page * size);
        params.add(size);

        return jdbcTemplate.query(sql.toString(), params.toArray(), materialRowMapper);
    }

}
