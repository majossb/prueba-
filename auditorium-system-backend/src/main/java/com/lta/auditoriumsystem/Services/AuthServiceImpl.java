package com.lta.auditoriumsystem.Services;

import com.lta.auditoriumsystem.dtos.LoginRequest;
import com.lta.auditoriumsystem.dtos.LoginResponse;
import com.lta.auditoriumsystem.dtos.RegisterRequest;

import com.lta.auditoriumsystem.Exceptions.ResourceNotFoundException;
import com.lta.auditoriumsystem.Entities.Rol;
import com.lta.auditoriumsystem.Entities.Usuario;
import com.lta.auditoriumsystem.Repositorios.RolRepository;
import com.lta.auditoriumsystem.Repositorios.UsuarioRepository;
import com.lta.auditoriumsystem.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenUtil.generateToken(authentication);
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new LoginResponse(
                jwt,
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                roles);
    }

    @Override
    @Transactional
    public void registerUser(RegisterRequest registerRequest) throws ResourceNotFoundException {
        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResourceNotFoundException("Error: El email ya está en uso!");
        }

        // Para crear un nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(registerRequest.getNombre());
        usuario.setApellido(registerRequest.getApellido());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Por defecto, asignar rol USER
        Rol userRole = rolRepository.findByNombre(Rol.ERole.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Error: Rol no encontrado."));
        Set<Rol> roles = new HashSet<>();
        roles.add(userRole);
        usuario.setRoles(roles);

        usuarioRepository.save(usuario);
    }
}