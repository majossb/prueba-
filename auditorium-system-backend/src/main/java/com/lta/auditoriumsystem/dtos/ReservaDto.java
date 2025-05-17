package com.lta.auditoriumsystem.dtos;

import com.lta.auditoriumsystem.Entities.Reserva;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDto {
    
    private Long id;
    
    private Long usuarioId;
    
    @NotNull(message = "El ID del auditorio es obligatorio")
    private Long auditorioId;
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDateTime fechaInicio;
    
    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    private LocalDateTime fechaFin;
    
    @NotBlank(message = "El título del evento es obligatorio")
    private String tituloEvento;
    
    @NotBlank(message = "La descripción del evento es obligatoria")
    private String descripcionEvento;
    
    @NotNull(message = "La cantidad de personas es obligatoria")
    @Min(value = 1, message = "La cantidad de personas debe ser mayor a 0")
    private Integer cantidadPersonas;
    
    private LocalDateTime fechaCreacion;
    
    private Reserva.EstadoReserva estado;
    
    // Datos adicionales para respuestas
    private String auditorioNombre;
    private String usuarioNombre;
    private String usuarioEmail;
}

