package com.lta.auditoriumsystem.Services;


    
import com.lta.auditoriumsystem.dtos.UsuarioDto;
import com.lta.auditoriumsystem.Exceptions.ResourceNotFoundException;

import java.util.List;

public interface UsuarioService {
    List<UsuarioDto> getAllUsuarios();
    UsuarioDto getUsuarioById(Long id) throws ResourceNotFoundException;
    UsuarioDto updateUsuario(Long id, UsuarioDto usuarioDto) throws ResourceNotFoundException;
    void deleteUsuario(Long id) throws ResourceNotFoundException;
    UsuarioDto getCurrentUsuario() throws ResourceNotFoundException;
    void addAdminRole(Long usuarioId) throws ResourceNotFoundException;
    void removeAdminRole(Long usuarioId) throws ResourceNotFoundException;
}


