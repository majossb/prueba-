package com.lta.auditoriumsystem.Controller;

import com.lta.auditoriumsystem.dtos.UsuarioDto;
import com.lta.auditoriumsystem.Services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioDto>> getAllUsuarios() {
        List<UsuarioDto> usuarios = usuarioService.getAllUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.esUsuarioActual(#id)")
    public ResponseEntity<UsuarioDto> getUsuarioById(@PathVariable Long id) {
        try {
            UsuarioDto usuario = usuarioService.getUsuarioById(id);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioDto> getCurrentUsuario() {
        try {
            UsuarioDto usuario = usuarioService.getCurrentUsuario();
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.esUsuarioActual(#id)")
    public ResponseEntity<UsuarioDto> updateUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioDto usuarioDto) {
        try {
            UsuarioDto updatedUsuario = usuarioService.updateUsuario(id, usuarioDto);
            return ResponseEntity.ok(updatedUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.esUsuarioActual(#id)")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        try {
            usuarioService.deleteUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/add-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addAdminRole(@PathVariable Long id) {
        try {
            usuarioService.addAdminRole(id);
            return ResponseEntity.ok("Rol ADMIN asignado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/remove-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> removeAdminRole(@PathVariable Long id) {
        try {
            usuarioService.removeAdminRole(id);
            return ResponseEntity.ok("Rol ADMIN removido exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
