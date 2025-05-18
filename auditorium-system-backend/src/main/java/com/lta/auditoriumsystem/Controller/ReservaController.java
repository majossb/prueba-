package com.lta.auditoriumsystem.Controller;

import com.lta.auditoriumsystem.dtos.ReservaDto;
import com.lta.auditoriumsystem.Services.ReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservaDto>> getAllReservas() {
        List<ReservaDto> reservas = reservaService.getAllReservas();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaDto> getReservaById(@PathVariable Long id) {
        try {
            ReservaDto reserva = reservaService.getReservaById(id);
            return ResponseEntity.ok(reserva);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/mis-reservas")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ReservaDto>> getMisReservas(Principal principal) {
        List<ReservaDto> reservas = reservaService.getReservasByUsuario(principal.getName());
        return ResponseEntity.ok(reservas);
    }

    @PostMapping
    public ResponseEntity<ReservaDto> createReserva(@Valid @RequestBody ReservaDto reservaDto) {
        try {
            ReservaDto nuevaReserva = reservaService.createReserva(reservaDto);
            return new ResponseEntity<>(nuevaReserva, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaDto> updateReserva(@PathVariable Long id, @Valid @RequestBody ReservaDto reservaDto) {
        try {
            ReservaDto updatedReserva = reservaService.updateReserva(id, reservaDto);
            return ResponseEntity.ok(updatedReserva);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserva(@PathVariable Long id) {
        try {
            reservaService.deleteReserva(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/confirmar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> confirmarReserva(@PathVariable Long id) {
        try {
            reservaService.confirmarReserva(id);
            return ResponseEntity.ok("Reserva confirmada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<String> cancelarReserva(@PathVariable Long id) {
        try {
            reservaService.cancelarReserva(id);
            return ResponseEntity.ok("Reserva cancelada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/check-disponibilidad")
    public ResponseEntity<Boolean> checkDisponibilidad(
            @RequestParam Long auditorioId,
            @Valid @RequestBody ReservaDto reservaDto) {
        boolean disponible = reservaService.isAuditorioDisponible(auditorioId, reservaDto);
        return ResponseEntity.ok(disponible);
    }
} 
