package com.pe.recepcion.controller;

import com.pe.recepcion.model.InvitacionEntity;
import com.pe.recepcion.repository.InvitationRepository;
import com.pe.recepcion.service.GenerationQrService;
import com.pe.recepcion.service.InvitationRecepService;
import com.pe.recepcion.service.WsInvitationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final GenerationQrService generarQRGeneral;

    // 🔹 Listar todas las invitaciones
    @GetMapping
    public List<InvitacionEntity> listarTodas() {
        return servicio.listar();
    }


    @PostMapping("/qr-entrada")
    public ResponseEntity<String> generarQREntrada() {
        try {
            generarQRGeneral.generarQREntrada(); // Reutilizas el mismo service
            return ResponseEntity.ok("✅ QR de entrada generado con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error al generar el QR de entrada.");
        }
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
        if (opt.isEmpty()) return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("❌ El invitado no fue encontrado. ¿Quizás fue recién agregado?");

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