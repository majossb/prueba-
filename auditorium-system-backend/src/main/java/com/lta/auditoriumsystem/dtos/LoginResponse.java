package com.lta.auditoriumsystem.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tipo = "Bearer";
    private Long id;
    private String email;
    private String nombre;
    private List<String> roles;
    
    public LoginResponse(String token, Long id, String email, String nombre, List<String> roles) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.roles = roles;
    }
}