package com.lta.auditoriumsystem.Services;


import com.lta.auditoriumsystem.dtos.ReservaDto;
import com.lta.auditoriumsystem.Entities.Auditorio;
import com.lta.auditoriumsystem.Entities.Reserva;
import com.lta.auditoriumsystem.Entities.Usuario;
import com.lta.auditoriumsystem.Exceptions.ResourceNotFoundException;
import com.lta.auditoriumsystem.Repositorios.AuditorioRepository;
import com.lta.auditoriumsystem.Repositorios.ReservaRepository;
import com.lta.auditoriumsystem.Repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuditorioRepository auditorioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ReservaDto> getAllReservas() {
        return reservaRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaDto getReservaById(Long id) throws ResourceNotFoundException {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));
        
        // Verificar si el usuario actual es admin o el dueño de la reserva
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isOwner = reserva.getUsuario().getEmail().equals(auth.getName());
        
        if (!isAdmin && !isOwner) {
            throw new ResourceNotFoundException("No tienes permiso para ver esta reserva");
        }
        
        return convertToDto(reserva);
    }

    @Override
    @Transactional
    public ReservaDto createReserva(ReservaDto reservaDto) throws ResourceNotFoundException {
        // Obtener usuario actual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Verificar disponibilidad del auditorio
        if (!isAuditorioDisponible(reservaDto.getAuditorioId(), reservaDto)) {
            throw new ResourceNotFoundException("El auditorio no está disponible en el horario seleccionado");
        }

        // Obtener auditorio
        Auditorio auditorio = auditorioRepository.findById(reservaDto.getAuditorioId())
                .orElseThrow(() -> new ResourceNotFoundException("Auditorio no encontrado con id: " + reservaDto.getAuditorioId()));

        // Validar capacidad
        if (reservaDto.getCantidadPersonas() > auditorio.getCapacidad()) {
            throw new ResourceNotFoundException("La cantidad de personas excede la capacidad del auditorio");
        }

        // Crear reserva
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setAuditorio(auditorio);
        reserva.setFechaInicio(reservaDto.getFechaInicio());
        reserva.setFechaFin(reservaDto.getFechaFin());
        reserva.setTituloEvento(reservaDto.getTituloEvento());
        reserva.setDescripcionEvento(reservaDto.getDescripcionEvento());
        reserva.setCantidadPersonas(reservaDto.getCantidadPersonas());
        reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);
        reserva.setFechaCreacion(LocalDateTime.now());

        Reserva savedReserva = reservaRepository.save(reserva);
        return convertToDto(savedReserva);
    }

    @Override
    @Transactional
    public ReservaDto updateReserva(Long id, ReservaDto reservaDto) throws ResourceNotFoundException {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));
        
        // Verificar permiso
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isOwner = reserva.getUsuario().getEmail().equals(auth.getName());
        
        if (!isAdmin && !isOwner) {
            throw new ResourceNotFoundException("No tienes permiso para actualizar esta reserva");
        }
        
        // Solo permitir actualizar si está pendiente
        if (reserva.getEstado() != Reserva.EstadoReserva.PENDIENTE) {
            throw new ResourceNotFoundException("Solo se pueden actualizar reservas en estado PENDIENTE");
        }
 // Verificar disponibilidad si cambia auditorio o fechas
        if ((reservaDto.getAuditorioId() != null && !reserva.getAuditorio().getId().equals(reservaDto.getAuditorioId())) || 
            (reservaDto.getFechaInicio() != null && !reserva.getFechaInicio().equals(reservaDto.getFechaInicio())) || 
            (reservaDto.getFechaFin() != null && !reserva.getFechaFin().equals(reservaDto.getFechaFin()))) {
            
            Long auditorioId = reservaDto.getAuditorioId() != null ? reservaDto.getAuditorioId() : reserva.getAuditorio().getId();
            LocalDateTime fechaInicio = reservaDto.getFechaInicio() != null ? reservaDto.getFechaInicio() : reserva.getFechaInicio();
            LocalDateTime fechaFin = reservaDto.getFechaFin() != null ? reservaDto.getFechaFin() : reserva.getFechaFin();
            
            ReservaDto tempReserva = new ReservaDto();
            tempReserva.setAuditorioId(auditorioId);
            tempReserva.setFechaInicio(fechaInicio);
            tempReserva.setFechaFin(fechaFin);
            
            // Ignorar la reserva actual en la verificación
            if (!isAuditorioDisponibleParaModificar(auditorioId, tempReserva, id)) {
                throw new ResourceNotFoundException("El auditorio no está disponible en el horario seleccionado");
            }
            
            // Actualizar auditorio si cambió
            if (reservaDto.getAuditorioId() != null && !reserva.getAuditorio().getId().equals(reservaDto.getAuditorioId())) {
              Auditorio nuevoAuditorio = auditorioRepository.findById(reservaDto.getAuditorioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Auditorio no encontrado con id: " + reservaDto.getAuditorioId()));
                reserva.setAuditorio(nuevoAuditorio);
            }
        }
        
        // Actualizar resto de campos
        if (reservaDto.getFechaInicio() != null) {
            reserva.setFechaInicio(reservaDto.getFechaInicio());
        }
        
        if (reservaDto.getFechaFin() != null) {
            reserva.setFechaFin(reservaDto.getFechaFin());
        }
        
        if (reservaDto.getTituloEvento() != null) {
            reserva.setTituloEvento(reservaDto.getTituloEvento());
        }
        
        if (reservaDto.getDescripcionEvento() != null) {
            reserva.setDescripcionEvento(reservaDto.getDescripcionEvento());
        }
        
        if (reservaDto.getCantidadPersonas() != null) {
            // Verificar capacidad del auditorio
            if (reservaDto.getCantidadPersonas() > reserva.getAuditorio().getCapacidad()) {
                throw new ResourceNotFoundException("La cantidad de personas excede la capacidad del auditorio");
            }
            reserva.setCantidadPersonas(reservaDto.getCantidadPersonas());
        }
        
        Reserva updatedReserva = reservaRepository.save(reserva);
        return convertToDto(updatedReserva);
    }

    @Override
    @Transactional
    public void deleteReserva(Long id) throws ResourceNotFoundException {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));
        
        // Verificar permiso
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isOwner = reserva.getUsuario().getEmail().equals(auth.getName());
        
        if (!isAdmin && !isOwner) {
            throw new ResourceNotFoundException("No tienes permiso para eliminar esta reserva");
        }
        
        reservaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaDto> getReservasByUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return reservaRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void confirmarReserva(Long id) throws ResourceNotFoundException {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));
        
        // Solo los administradores pueden confirmar reservas
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        
        if (!isAdmin) {
            throw new ResourceNotFoundException("No tienes permiso para confirmar reservas");
        }
        
        // Solo se pueden confirmar reservas pendientes
        if (reserva.getEstado() != Reserva.EstadoReserva.PENDIENTE) {
            throw new ResourceNotFoundException("Solo se pueden confirmar reservas en estado PENDIENTE");
        }
        
        reserva.setEstado(Reserva.EstadoReserva.CONFIRMADA);
        reservaRepository.save(reserva);
    }

    @Override
    @Transactional
    public void cancelarReserva(Long id) throws ResourceNotFoundException {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));
        
        // Verificar permiso
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isOwner = reserva.getUsuario().getEmail().equals(auth.getName());
        
        if (!isAdmin && !isOwner) {
            throw new ResourceNotFoundException("No tienes permiso para cancelar esta reserva");
        }
        
        // Solo se pueden cancelar reservas pendientes o confirmadas
        if (reserva.getEstado() != Reserva.EstadoReserva.PENDIENTE && reserva.getEstado() != Reserva.EstadoReserva.CONFIRMADA) {
            throw new ResourceNotFoundException("Solo se pueden cancelar reservas en estado PENDIENTE o CONFIRMADA");
        }
        
        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
    }

    @Override
    public boolean isAuditorioDisponible(Long auditorioId, ReservaDto reservaDto) {
        // Verificar que las fechas sean válidas
        if (reservaDto.getFechaInicio().isAfter(reservaDto.getFechaFin())) {
            return false;
        }
        
        // Verificar que no haya otra reserva confirmada en el mismo horario
        return !reservaRepository.existsByAuditorioIdAndFechaInicioBeforeAndFechaFinAfterAndEstado(
                auditorioId, 
                reservaDto.getFechaFin(), 
                reservaDto.getFechaInicio(),
                Reserva.EstadoReserva.CONFIRMADA);
    }
    
    private boolean isAuditorioDisponibleParaModificar(Long auditorioId, ReservaDto reservaDto, Long reservaId) {
        // Verificar que las fechas sean válidas
        if (reservaDto.getFechaInicio().isAfter(reservaDto.getFechaFin())) {
            return false;
        }
        
        // Buscar reservas confirmadas que se solapen con el horario
        List<Reserva> reservasSolapadas = reservaRepository.findAll().stream()
                .filter(r -> r.getAuditorio().getId().equals(auditorioId))
                .filter(r -> !r.getId().equals(reservaId)) // Excluir la reserva actual
                .filter(r -> r.getEstado() == Reserva.EstadoReserva.CONFIRMADA)
                .filter(r -> r.getFechaInicio().isBefore(reservaDto.getFechaFin()) && r.getFechaFin().isAfter(reservaDto.getFechaInicio()))
                .collect(Collectors.toList());
        
        return reservasSolapadas.isEmpty();
    }

    private ReservaDto convertToDto(Reserva reserva) {
        ReservaDto reservaDto = new ReservaDto();
        reservaDto.setId(reserva.getId());
        reservaDto.setUsuarioId(reserva.getUsuario().getId());
        reservaDto.setAuditorioId(reserva.getAuditorio().getId());
        reservaDto.setFechaInicio(reserva.getFechaInicio());
        reservaDto.setFechaFin(reserva.getFechaFin());
        reservaDto.setTituloEvento(reserva.getTituloEvento());
        reservaDto.setDescripcionEvento(reserva.getDescripcionEvento());
        reservaDto.setCantidadPersonas(reserva.getCantidadPersonas());
        reservaDto.setFechaCreacion(reserva.getFechaCreacion());
        reservaDto.setEstado(reserva.getEstado());
        
        // Datos extras para mejor respuesta
        reservaDto.setAuditorioNombre(reserva.getAuditorio().getNombre());
        reservaDto.setUsuarioNombre(reserva.getUsuario().getNombre() + " " + reserva.getUsuario().getApellido());
        reservaDto.setUsuarioEmail(reserva.getUsuario().getEmail());
        
        return reservaDto;
    }
}
