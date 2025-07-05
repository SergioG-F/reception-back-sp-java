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
        boolean asistira = Boolean.parseBoolean(datos.get("asistio"));

        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Debes proporcionar tu nombre.");
        }

        Optional<InvitacionEntity> opt = invitationRepository.findByNombre(nombre);

        if (opt.isPresent()) {
            InvitacionEntity inv = opt.get();
            if (inv.isAsistio()) {
                return ResponseEntity.badRequest().body("⚠️ Ya registraste tu asistencia.");
            }
            inv.setAsistio(true);
            inv.setAsistio(asistira);
            invitationRepository.save(inv);
            notificationService.notificarConfirmacion(inv);
            return ResponseEntity.ok("✅ Confirmación  registrada correctamente.");
        }

        InvitacionEntity nuevo = new InvitacionEntity();
        nuevo.setNombre(nombre);
        nuevo.setAsistio(asistira);
        nuevo.setAsistio(true);
        invitationRepository.save(nuevo);
        notificationService.notificarConfirmacion(nuevo);
        return ResponseEntity.ok("✅ Confirmación  registrada correctamente.");
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