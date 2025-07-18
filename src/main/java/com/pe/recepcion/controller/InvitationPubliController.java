package com.pe.recepcion.controller;

import com.pe.recepcion.model.InvitacionEntity;
import com.pe.recepcion.repository.InvitationRepository;
import com.pe.recepcion.service.WsInvitationService;
import com.pe.recepcion.util.CodigoMatrimonioUnico;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/invitaciones")

@Getter
@Setter
@Data
public class InvitationPubliController {
    private final InvitationRepository invitationRepository;
    private final WsInvitationService notificationService;
    private final SimpMessagingTemplate messagingTemplate; // ✅ Para WebSocket

    public InvitationPubliController(InvitationRepository invitationRepository, WsInvitationService notificationService,
            SimpMessagingTemplate messagingTemplate) {
        this.invitationRepository = invitationRepository;
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
    }

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

            String fecha;
            if (invitacion.getFechaConfirmacion() != null) {
                // Convierte LocalDateTime a ZonedDateTime en zona del servidor (GMT aquí)
                ZonedDateTime zonedFechaConfirmacion = invitacion.getFechaConfirmacion()
                        .atZone(ZoneId.of("GMT"))
                        // Cambia la zona a America/Lima, ajustando la hora correctamente
                        .withZoneSameInstant(ZoneId.of("America/Lima"));
                // Formatea la fecha para mostrar
                fecha = zonedFechaConfirmacion.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'a las' HH:mm"));
            } else {
                fecha = "previamente";
            }
            return ResponseEntity.ok(Map.of(
                    "mensaje", "⚠️ Ya registraste tu asistencia el " + fecha + ".",
                    "nuevoRegistro", false
            ));
        } else {
            invitacion = new InvitacionEntity();
            invitacion.setNombre(nombre);
            invitacion.setFecha(LocalDateTime.now()); // fecha de creación
        }

        // ✅ Solo generar código si va a asistir y no lo tiene aún
        if (asistira && (invitacion.getCodigoMatrimonio() == null || invitacion.getCodigoMatrimonio().isEmpty())) {
            String codigo = CodigoMatrimonioUnico.generarCodigo("MSM");
            invitacion.setCodigoMatrimonio(codigo);
        }

        invitacion.setAsistio(asistira);
        invitacion.setFechaConfirmacion(LocalDateTime.now());
        invitationRepository.save(invitacion);

        notificationService.notificarConfirmacion(invitacion);
        // ✅ Emitir actualización por WebSocket
        messagingTemplate.convertAndSend("/topic/invitaciones", invitacion);

        // ✅ Mensaje final
        String mensaje = asistira
                ? "🎉 Gracias " + nombre + " por confirmar tu asistencia.\n" +
                "🎟️ Tu código es: " + invitacion.getCodigoMatrimonio() + "\n✅ Guárdalo para presentarlo el día del evento."
                : "💐 Gracias " + nombre + " por avisarnos. Te tendremos presente en nuestros corazones.";
        // ✅ RESPUESTA según la decisión
        if (!asistira) {
            return ResponseEntity.ok(Map.of("mensaje", mensaje));
        }
        return ResponseEntity.ok(Map.of(
                "mensaje", mensaje,
                "codigoMatrimonio", invitacion.getCodigoMatrimonio(),
                "nuevoRegistro", true
        ));
    }

    @PostMapping("/marcar-entrada")
    public ResponseEntity<?> registrarEntradaFlexible(@RequestBody Map<String, String> body) {
        String codigo = body.get("codigoMatrimonio");
        String nombre = body.get("nombre");
        String modo = body.getOrDefault("modoEntrada", "manual");

        InvitacionEntity invitado = null;

        // Buscar por código
        // 🔧 CAMBIO: Aceptar código con o sin prefijo MSM-
        if (codigo != null && !codigo.isBlank()) {
            String codigoLimpio = codigo.trim().toUpperCase();
            if (!codigoLimpio.startsWith("MSM-")) {
                codigoLimpio = "MSM-" + codigoLimpio;
            }
            invitado = invitationRepository.findByCodigoMatrimonio(codigoLimpio).orElse(null);
        }

        // Si no se encontró por código, buscar por nombre
        if (invitado == null && nombre != null && !nombre.isBlank()) {
            invitado = invitationRepository.findByNombre(nombre.trim()).orElse(null);
        }

        if (invitado == null) {
            return ResponseEntity.badRequest().body("❌ Invitado no encontrado.");
        }

        // Verificar si ya marcó entrada
        if (Boolean.TRUE.equals(invitado.getPresente())) {
            return ResponseEntity.badRequest().body("YA_REGISTRADO: Entrada ya fue registrada previamente.");
        }

        // Registrar entrada (aunque no haya confirmado asistencia)
        invitado.setPresente(true);
        ZonedDateTime nowLima = ZonedDateTime.now(ZoneId.of("America/Lima"));
        invitado.setFechaEntrada(nowLima.toLocalDateTime());  // ✅ aquí conviertes a LocalDateTime
        invitado.setModoEntrada(modo);

        invitationRepository.save(invitado);
        notificationService.notificarConfirmacion(invitado);
        // Formatear esa misma hora para mostrar en el mensaje
        String fechaFormateada = nowLima.format(
                DateTimeFormatter.ofPattern("dd 'de' MMMM 'a las' HH:mm")
        );

        // Mensaje según si confirmó o no
        String mensaje = Boolean.TRUE.equals(invitado.getAsistio())
                ? "🎉 Entrada registrada exitosamente el " + fechaFormateada  + ". ¡Bienvenido/a! "
                : "⚠️ No confirmaste asistencia, pero te registramos la entrada el " + fechaFormateada + ". ¡Bienvenido/a igual!";

        return ResponseEntity.ok(Map.of(
                "mensaje", mensaje,
                "nombre", invitado.getNombre(),
                "modoEntrada", modo,
                "codigoMatrimonio", invitado.getCodigoMatrimonio()  // ✅ Agregado

        ));
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

    @GetMapping("/qr-entrada")
    public ResponseEntity<ByteArrayResource> obtenerQREntrada() throws IOException {
        Path path = Paths.get("qr/qr-entrada.png");
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