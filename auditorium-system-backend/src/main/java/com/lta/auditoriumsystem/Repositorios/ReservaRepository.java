package com.lta.auditoriumsystem.Repositorios;

import com.lta.auditoriumsystem.Entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuarioId(Long usuarioId);

    List<Reserva> findByAuditorioId(Long auditorioId);

    List<Reserva> findByEstado(Reserva.EstadoReserva estado);

    List<Reserva> findByFechaInicioAfter(LocalDateTime fecha);

    boolean existsByAuditorioIdAndFechaInicioBeforeAndFechaFinAfterAndEstado(
            Long auditorioId, LocalDateTime end, LocalDateTime start, Reserva.EstadoReserva estado);
}
