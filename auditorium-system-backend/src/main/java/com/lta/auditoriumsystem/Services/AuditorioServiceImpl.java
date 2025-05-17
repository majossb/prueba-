package com.lta.auditoriumsystem.Services;

import com.lta.auditoriumsystem.dtos.AuditorioDto;
import com.lta.auditoriumsystem.Exceptions.ResourceNotFoundException;
import com.lta.auditoriumsystem.Entities.Auditorio;
import com.lta.auditoriumsystem.Repositorios.AuditorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditorioServiceImpl implements AuditorioService {

    @Autowired
    private AuditorioRepository auditorioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AuditorioDto> getAllAuditorios() {
        return auditorioRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AuditorioDto getAuditorioById(Long id) throws ResourceNotFoundException {
        Auditorio auditorio = auditorioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auditorio no encontrado con id: " + id));
        return convertToDto(auditorio);
    }

    @Override
    @Transactional
    public AuditorioDto createAuditorio(AuditorioDto AuditorioDto) {
        Auditorio auditorio = convertToEntity(AuditorioDto);
        Auditorio savedAuditorio = auditorioRepository.save(auditorio);
        return convertToDto(savedAuditorio);
    }

    @Override
    @Transactional
    public AuditorioDto updateAuditorio(Long id, AuditorioDto auditorioDto) throws ResourceNotFoundException {
        Auditorio auditorio = auditorioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auditorio no encontrado con id: " + id));

        auditorio.setNombre(auditorioDto.getNombre());
        auditorio.setDescripcion(auditorioDto.getDescripcion());
        auditorio.setCapacidad(auditorioDto.getCapacidad());
        auditorio.setPrecio(auditorioDto.getPrecio());
        auditorio.setUbicacion(auditorioDto.getUbicacion());
        auditorio.setImagenUrl(auditorioDto.getImagenUrl());

        Auditorio updatedAuditorio = auditorioRepository.save(auditorio);
        return convertToDto(updatedAuditorio);
    }

    @Override
    @Transactional
    public void deleteAuditorio(Long id) throws ResourceNotFoundException {
        if (!auditorioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Auditorio no encontrado con id: " + id);
        }
        auditorioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditorioDto> getAvailableAuditorios(LocalDateTime start, LocalDateTime end, Integer capacidad) {
        List<Auditorio> auditorios;

        if (capacidad != null && capacidad > 0) {
            auditorios = auditorioRepository.findAvailableAuditorios(start, end);
            auditorios = auditorios.stream()
                    .filter(a -> a.getCapacidad() >= capacidad)
                    .collect(Collectors.toList());
        } else {
            auditorios = auditorioRepository.findAvailableAuditorios(start, end);
        }

        return auditorios.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private AuditorioDto convertToDto(Auditorio auditorio) {
        AuditorioDto auditorioDto = new AuditorioDto();
        auditorioDto.setId(auditorio.getId());
        auditorioDto.setNombre(auditorio.getNombre());
        auditorioDto.setDescripcion(auditorio.getDescripcion());
        auditorioDto.setCapacidad(auditorio.getCapacidad());
        auditorioDto.setPrecio(auditorio.getPrecio());
        auditorioDto.setUbicacion(auditorio.getUbicacion());
        auditorioDto.setImagenUrl(auditorio.getImagenUrl());
        return auditorioDto;
    }

    private Auditorio convertToEntity(AuditorioDto auditorioDto) {
        Auditorio auditorio = new Auditorio();
        auditorio.setNombre(auditorioDto.getNombre());
        auditorio.setDescripcion(auditorioDto.getDescripcion());
        auditorio.setCapacidad(auditorioDto.getCapacidad());
        auditorio.setPrecio(auditorioDto.getPrecio());
        auditorio.setUbicacion(auditorioDto.getUbicacion());
        auditorio.setImagenUrl(auditorioDto.getImagenUrl());
        return auditorio;
    }
}