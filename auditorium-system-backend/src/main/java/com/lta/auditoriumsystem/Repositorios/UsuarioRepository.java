package com.lta.auditoriumsystem.Repositorios;

import com.lta.auditoriumsystem.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Boolean existsByEmail(String email);

    List<Usuario> findByIdIn(List<Long> ids);
}
