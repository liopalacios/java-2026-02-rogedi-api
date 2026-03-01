package com.zennitech.digital.repository;

import com.zennitech.digital.model.TecnicoModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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

    public List<TecnicoModel> buscarPorNombreODocumento(String nombre, String documento, String tipo) {
        StringBuilder sql = new StringBuilder("SELECT * FROM rogedibd.tecnicos WHERE 1=1 and activo=true ");
        List<Object> params = new ArrayList<>();

        // Búsqueda por nombre (si se proporciona)
        if (nombre != null && !nombre.trim().isEmpty()) {
            sql.append(" AND LOWER(nombres) LIKE LOWER(?)");
            params.add("%" + nombre + "%");
        }

        // Búsqueda por documento (si se proporciona)
        if (documento != null && !documento.trim().isEmpty()) {
            sql.append(" AND numero_documento = ?");
            params.add(documento);

            // Si también se proporciona tipo, filtrar por tipo
            if (tipo != null && !tipo.trim().isEmpty()) {
                sql.append(" AND tipo_documento = ?");
                params.add(tipo);
            }
        }
        System.out.println(nombre+" "+tipo+" "+documento);
        System.out.println(sql.toString());
        return jdbcTemplate.query(
                sql.toString(),
                new BeanPropertyRowMapper<>(TecnicoModel.class),
                params.toArray()
        );
    }

    public int desactivar(Long id) {
        String sql = "UPDATE rogedibd.tecnicos SET activo = false WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public List<TecnicoModel> buscarPorEmailTelefono(String email, String telefono) {
        StringBuilder sql = new StringBuilder("SELECT * FROM rogedibd.tecnicos WHERE 1=1 and activo=true ");
        List<Object> params = new ArrayList<>();

        // Búsqueda por nombre (si se proporciona)
        if (email != null && !email.trim().isEmpty()) {
            sql.append(" AND LOWER(email) LIKE LOWER(?)");
            params.add( email );

            sql.append(" or telefono = ?");
            params.add(telefono);

        }
        System.out.println(email+" "+telefono+" ");
        System.out.println(sql);
        return jdbcTemplate.query(
                sql.toString(),
                new BeanPropertyRowMapper<>(TecnicoModel.class),
                params.toArray()
        );
    }
}
