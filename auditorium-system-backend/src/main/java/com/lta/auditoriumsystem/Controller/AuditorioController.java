package com.lta.auditoriumsystem.Controller;

import com.lta.auditoriumsystem.dtos.AuditorioDto;
import com.lta.auditoriumsystem.Services.AuditorioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auditorios")
public class AuditorioController {

    @Autowired
    private AuditorioService auditorioService;

    @GetMapping
    public ResponseEntity<List<AuditorioDto>> getAllAuditorios() {
        List<AuditorioDto> auditorios = auditorioService.getAllAuditorios();
        return ResponseEntity.ok(auditorios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditorioDto> getAuditorioById(@PathVariable Long id) {
        try {
            AuditorioDto auditorio = auditorioService.getAuditorioById(id);
            return ResponseEntity.ok(auditorio);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<AuditorioDto>> getAvailableAuditorios(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(required = false) Integer capacidad) {
        
        List<AuditorioDto> auditorios = auditorioService.getAvailableAuditorios(fechaInicio, fechaFin, capacidad);
        return ResponseEntity.ok(auditorios);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuditorioDto> createAuditorio(@Valid @RequestBody AuditorioDto auditorioDto) {
        AuditorioDto nuevoAuditorio = auditorioService.createAuditorio(auditorioDto);
        return new ResponseEntity<>(nuevoAuditorio, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuditorioDto> updateAuditorio(@PathVariable Long id, @Valid @RequestBody AuditorioDto auditorioDTO) {
        try {
            AuditorioDto updatedAuditorio = auditorioService.updateAuditorio(id, auditorioDTO);
            return ResponseEntity.ok(updatedAuditorio);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAuditorio(@PathVariable Long id) {
        try {
            auditorioService.deleteAuditorio(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}