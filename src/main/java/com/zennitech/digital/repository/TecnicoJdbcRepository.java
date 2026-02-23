package com.zennitech.digital.repository;

import com.zennitech.digital.model.TecnicoModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
@RequiredArgsConstructor
public class TecnicoJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public int crear(TecnicoModel tecnico) {
        String sql = "INSERT INTO rogedibd.tecnicos (nombres, apellidos, tipo_documento, numero_documento, telefono, " +
                "email, direccion, fecha_nacimiento, costo, activo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                tecnico.getNombres(),
                tecnico.getApellidos(),
                tecnico.getTipoDocumento(),
                tecnico.getNumeroDocumento(),
                tecnico.getTelefono(),
                tecnico.getEmail(),
                tecnico.getDireccion(),
                tecnico.getFechaNacimiento(),
                tecnico.getCosto(),
                true
        );
    }

    public int modificar(Long id, TecnicoModel tecnico) {
        String sql = "UPDATE rogedibd.tecnicos SET nombres=?, apellidos=?, tipo_documento=?, numero_documento=?, telefono=?, email=?, direccion=?, fecha_nacimiento=?, costo=? WHERE id=?";
        return jdbcTemplate.update(sql,
                tecnico.getNombres(),
                tecnico.getApellidos(),
                tecnico.getTipoDocumento(),
                tecnico.getNumeroDocumento(),
                tecnico.getTelefono(),
                tecnico.getEmail(),
                tecnico.getDireccion(),
                tecnico.getFechaNacimiento(),
                tecnico.getCosto(),
                id
        );
    }

    public List<TecnicoModel> listar() {
        String sql = "SELECT * FROM rogedibd.tecnicos WHERE activo = true order by " +
                "COALESCE(fecha_modificacion, fecha_creacion) DESC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TecnicoModel.class));
    }

    public Optional<TecnicoModel> buscarPorId(Long id) {
        String sql = "SELECT * FROM rogedibd.tecnicos WHERE id = ?";
        List<TecnicoModel> result = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TecnicoModel.class), id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public List<TecnicoModel> buscarPorNombreODocumento(String nombre, String documento) {
        String sql = "SELECT * FROM rogedibd.tecnicos WHERE (LOWER(nombres) LIKE LOWER(?) OR numero_documento = ?)";
        return jdbcTemplate.query(sql,
                new BeanPropertyRowMapper<>(TecnicoModel.class),
                "%" + nombre + "%",
                documento
        );
    }

    public int desactivar(Long id) {
        String sql = "UPDATE rogedibd.tecnicos SET activo = false WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
