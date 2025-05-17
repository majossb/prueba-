package com.lta.auditoriumsystem.Repositorios;

import com.lta.auditoriumsystem.Entities.Auditorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditorioRepository extends JpaRepository<Auditorio, Long> {
    List<Auditorio> findByCapacidadGreaterThanEqual(Integer capacidad);

    @Query("SELECT a FROM Auditorio a WHERE a.id NOT IN " +
            "(SELECT r.auditorio.id FROM Reserva r WHERE " +
            "((r.fechaInicio <= ?2 AND r.fechaFin >= ?1) OR " +
            "(r.fechaInicio >= ?1 AND r.fechaInicio <= ?2)) AND " +
            "r.estado = 'CONFIRMADA')")
    List<Auditorio> findAvailableAuditorios(LocalDateTime start, LocalDateTime end);
}
