package com.lta.auditoriumsystem.Services;

import com.lta.auditoriumsystem.dtos.AuditorioDto;
import com.lta.auditoriumsystem.Exceptions.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditorioService {
    List<AuditorioDto> getAllAuditorios();
    AuditorioDto getAuditorioById(Long id) throws ResourceNotFoundException;
    AuditorioDto createAuditorio(AuditorioDto auditorioDTO);
    AuditorioDto updateAuditorio(Long id, AuditorioDto auditorioDTO) throws ResourceNotFoundException;
    void deleteAuditorio(Long id) throws ResourceNotFoundException;
    List<AuditorioDto> getAvailableAuditorios(LocalDateTime start, LocalDateTime end, Integer capacidad);
}
