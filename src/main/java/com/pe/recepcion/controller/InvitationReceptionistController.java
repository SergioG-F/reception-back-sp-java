package com.pe.recepcion.controller;

import com.pe.recepcion.model.InvitacionEntity;
import com.pe.recepcion.repository.InvitationRepository;
import com.pe.recepcion.service.InvitationRecepService;
import com.pe.recepcion.service.WsInvitationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recepcionista/invitaciones")
@AllArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_ADMIN','RECEPCIONISTA')")
public class InvitationReceptionistController {
    private final InvitationRecepService servicio;
    private final InvitationRepository invitationRepository;
    private final WsInvitationService notificationService;

    // 🔹 Listar todas las invitaciones
    @GetMapping
    public List<InvitacionEntity> listarTodas() {
        return servicio.listar();
    }

    // 🔹 Buscar invitación por ID
    @GetMapping("/{id}")
    public ResponseEntity<InvitacionEntity> obtenerPorId(@PathVariable String id) {
        return servicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Verificar código QR
    @GetMapping("/verificar/{codigoQR}")
    public ResponseEntity<InvitacionEntity> verificar(@PathVariable String codigoQR) {
        return servicio.buscarPorId(codigoQR)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Marcar asistencia
    @PostMapping("/asistencia/{id}")
    public ResponseEntity<InvitacionEntity> marcarAsistencia(@PathVariable String id) {
        try {
            return ResponseEntity.ok(servicio.marcarAsistencia(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/marcar-entrada-recepcionista/{id}")
    public ResponseEntity<?> registrarEntradaPorRecepcionista(@PathVariable String id) {
        Optional<InvitacionEntity> opt = invitationRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        InvitacionEntity invitado = opt.get();

        if (Boolean.TRUE.equals(invitado.getPresente())) {
            return ResponseEntity.badRequest().body("❗ Entrada ya registrada.");
        }

        invitado.setPresente(true);
        invitado.setFechaEntrada(LocalDateTime.now());
        invitado.setModoEntrada("recepcionista");

        invitationRepository.save(invitado);
        notificationService.notificarConfirmacion(invitado);

        return ResponseEntity.ok("✅ Entrada registrada por recepcionista");
    }
}