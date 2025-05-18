package com.lta.auditoriumsystem.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "testimonios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Testimonio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String cargo;

    @Column(nullable = false, length = 1000)
    private String comentario;

    @Column(nullable = false)
    private String imagen;  // ruta como "/images/persona.png"
}
