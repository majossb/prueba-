package com.lta.auditoriumsystem.Services;

import com.lta.auditoriumsystem.dtos.UsuarioDto;
import com.lta.auditoriumsystem.Exceptions.ResourceNotFoundException;
import com.lta.auditoriumsystem.Entities.Rol;
import com.lta.auditoriumsystem.Entities.Usuario;
import com.lta.auditoriumsystem.Repositorios.RolRepository;
import com.lta.auditoriumsystem.Repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService, UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDto> getAllUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDto getUsuarioById(Long id) throws ResourceNotFoundException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return convertToDto(usuario);
    }

    @Override
    @Transactional
    public UsuarioDto updateUsuario(Long id, UsuarioDto usuarioDto) throws ResourceNotFoundException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        
        usuario.setNombre(usuarioDto.getNombre());
        usuario.setApellido(usuarioDto.getApellido());
        
        Usuario updatedUsuario = usuarioRepository.save(usuario);
        return convertToDto(updatedUsuario);
    }

    @Override
    @Transactional
    public void deleteUsuario(Long id) throws ResourceNotFoundException {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDto getCurrentUsuario() throws ResourceNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        
        Usuario usuario = usuarioRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario actual no encontrado"));
        
        return convertToDto(usuario);
    }

    @Override
    @Transactional
    public void addAdminRole(Long usuarioId) throws ResourceNotFoundException {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId));
        
        Rol adminRole = rolRepository.findByNombre(Rol.ERole.ROLE_ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Rol ADMIN no encontrado"));
        
        usuario.getRoles().add(adminRole);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void removeAdminRole(Long usuarioId) throws ResourceNotFoundException {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId));
        
        Rol adminRole = rolRepository.findByNombre(Rol.ERole.ROLE_ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Rol ADMIN no encontrado"));
        
        usuario.getRoles().remove(adminRole);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + username));

        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getRoles().stream()
                        .map(rol -> new SimpleGrantedAuthority(rol.getNombre().name()))
                        .collect(Collectors.toList())
        );
    }

    private UsuarioDto convertToDto(Usuario usuario) {
        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setId(usuario.getId());
        usuarioDto.setNombre(usuario.getNombre());
        usuarioDto.setApellido(usuario.getApellido());
        usuarioDto.setEmail(usuario.getEmail());
        
        Set<String> roles = usuario.getRoles().stream()
                .map(rol -> rol.getNombre().name())
                .collect(Collectors.toSet());
        usuarioDto.setRoles(roles);
        
        return usuarioDto;
    }
}

