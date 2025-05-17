package com.lta.auditoriumsystem.Services;

import com.lta.auditoriumsystem.dtos.ReservaDto;
import com.lta.auditoriumsystem.Exceptions.ResourceNotFoundException;

import java.util.List;

public interface ReservaService {
    List<ReservaDto> getAllReservas();
    ReservaDto getReservaById(Long id) throws ResourceNotFoundException;
    ReservaDto createReserva(ReservaDto reservaDTO) throws ResourceNotFoundException;
    ReservaDto updateReserva(Long id, ReservaDto reservaDTO) throws ResourceNotFoundException;
    void deleteReserva(Long id) throws ResourceNotFoundException;
    List<ReservaDto> getReservasByUsuario();
    void confirmarReserva(Long id) throws ResourceNotFoundException;
    void cancelarReserva(Long id) throws ResourceNotFoundException;
    boolean isAuditorioDisponible(Long auditorioId, ReservaDto reservaDTO);
}
