package com.lta.auditoriumsystem.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "auditorio_id", nullable = false)
    private Auditorio auditorio;
    
    @Column(nullable = false)
    private LocalDateTime fechaInicio;
    
    @Column(nullable = false)
    private LocalDateTime fechaFin;
    
    @Column(nullable = false)
    private String tituloEvento;
    
    @Column(nullable = false)
    private String descripcionEvento;
    
    @Column(nullable = false)
    private Integer cantidadPersonas;
    
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado;
    
    public enum EstadoReserva {
        PENDIENTE,
        CONFIRMADA,
        CANCELADA,
        COMPLETADA
    }
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
