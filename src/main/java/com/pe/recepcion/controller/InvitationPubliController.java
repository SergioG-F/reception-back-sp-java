package com.pe.recepcion.controller;

import com.pe.recepcion.model.InvitacionEntity;
import com.pe.recepcion.repository.InvitationRepository;
import com.pe.recepcion.service.WsInvitationService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/invitaciones")
@AllArgsConstructor
public class InvitationPubliController {
    private final InvitationRepository invitationRepository;
    private final WsInvitationService notificationService;

    @PostMapping("/confirmar-asistencia")
    public ResponseEntity<?> confirmarAsistencia(@RequestBody Map<String, String> datos) {
        String nombre = datos.get("nombre");
        String asistioStr = datos.get("asistio");

        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Debes proporcionar tu nombre.");
        }
        if (asistioStr == null) {
            return ResponseEntity.badRequest().body("❌ Debes indicar si asistirás o no.");
        }
        Boolean asistira = Boolean.parseBoolean(asistioStr);

        Optional<InvitacionEntity> opt = invitationRepository.findByNombre(nombre);
        InvitacionEntity invitacion;

        if (opt.isPresent()) {
            invitacion = opt.get();
            // ⚠️ Ya registró su respuesta antes
            if (invitacion.getAsistio() != null) {
                String fecha = invitacion.getFechaConfirmacion() != null
                        ? invitacion.getFechaConfirmacion().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'a las' HH:mm"))
                        : "previamente";

                return ResponseEntity.badRequest().body("⚠️ Ya registraste tu asistencia el " + fecha + ".");
            }


        } else {
            invitacion = new InvitacionEntity();
            invitacion.setNombre(nombre);
            invitacion.setFecha(LocalDateTime.now()); // fecha de creación
        }
        invitacion.setAsistio(asistira);                            // ✅ Guardar su decisión
        invitacion.setFechaConfirmacion(LocalDateTime.now());       // 🕒 Guardar cuándo lo hizo
        invitationRepository.save(invitacion);
        notificationService.notificarConfirmacion(invitacion);
        return ResponseEntity.ok("✅ Confirmación registrada correctamente.");
    }
    @GetMapping("/qr")
    public ResponseEntity<ByteArrayResource> obtenerQRGeneral() throws IOException {
        Path path = Paths.get("qr/qr-general.png");
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(resource);
    }

    @GetMapping("/todos")
    public List<InvitacionEntity> listarInvitaciones() {
        return invitationRepository.findAll();
    }
}