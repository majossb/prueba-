package com.lta.auditoriumsystem.Entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "auditorios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auditorio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String descripcion;
    
    @Column(nullable = false)
    private Integer capacidad;
    
    @Column(nullable = false)
    private BigDecimal precio;
    
    @Column(nullable = false)
    private String ubicacion;
    
    @Column(nullable = false)
    private String imagenUrl;
    
    @OneToMany(mappedBy = "auditorio", cascade = CascadeType.ALL)
    private Set<Reserva> reservas = new HashSet<>();
}