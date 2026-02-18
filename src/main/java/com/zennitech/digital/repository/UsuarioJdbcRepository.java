package com.zennitech.digital.repository;

import com.zennitech.digital.model.UsuarioModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UsuarioJdbcRepository {
    private final JdbcTemplate jdbc;

    public Optional<UsuarioModel> findByUsername(String username) {
        try {
            String sql = "SELECT id, username, password FROM usuario WHERE username = ?";
            UsuarioModel user = jdbc.queryForObject(sql, new Object[]{username}, (rs, rowNum) -> {
                UsuarioModel u = new UsuarioModel();
                u.setId(rs.getLong("id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                return u;
            });
            if (user == null) return null;
            // Obtener roles
            String rolesSql = "SELECT r.nombre FROM rol r " +
                    "INNER JOIN usuario_rol ur ON r.id = ur.rol_id " +
                    "WHERE ur.usuario_id = ?";
            List<String> roles = jdbc.queryForList(rolesSql, new Object[]{user.getId()}, String.class);
            user.setRoles(roles);

            // Obtener accesos
            String accesosSql = "SELECT ac.nombre FROM rol_accesos a " +
                    "INNER JOIN usuario_rol ur ON a.rol_id = ur.rol_id " +
                    "INNER JOIN accesos ac on a.acceso_id = ac.id " +
                    "WHERE ur.usuario_id = ?";
            List<String> accesos = jdbc.queryForList(accesosSql, new Object[]{user.getId()}, String.class);
            user.setAccesos(accesos);

            return Optional.of(user);

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
