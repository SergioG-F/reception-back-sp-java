package com.pe.recepcion.controller;

import com.pe.recepcion.dto.request.ConfirmarAsistenciaRequest;
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
import java.util.*;
import java.util.stream.Collectors;

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
    public ResponseEntity<?> confirmarAsistencia(@RequestBody ConfirmarAsistenciaRequest request) {
        List<String> nombres = request.getNombres();
        Boolean asistira = request.getAsistio();
        Integer cantidadGrupo = request.getCantidadGrupo();

        if (nombres == null || nombres.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Debes proporcionar al menos un nombre.");
        }

        // ✅ Validación: No se puede decir que son 3 si solo mandas 1 nombre
        if (asistira && cantidadGrupo != null && cantidadGrupo < nombres.size()) {
            return ResponseEntity.badRequest().body("❌ La cantidad del grupo no puede ser menor al número de nombres enviados.");
        }
        // ✅ NUEVA VALIDACIÓN: Evitar que se repita código entre individual y grupo
        boolean algunConfirmado = false;
        boolean algunEsGrupo = false;

        for (String nombre : nombres) {
            Optional<InvitacionEntity> existente = invitationRepository.findByNombre(nombre.trim());
            if (existente.isPresent()) {
                InvitacionEntity yaConfirmado = existente.get();
                if (yaConfirmado.getFechaConfirmacion() != null) {
                    algunConfirmado = true;
                    if (yaConfirmado.getCantidadGrupo() != null && yaConfirmado.getCantidadGrupo() > 1) {
                        algunEsGrupo = true;
                    }
                }
            }
        }

        if (algunConfirmado) {
            return ResponseEntity.ok(Map.of(
                    "mensaje", "⚠️ Parte del grupo ya confirmó anteriormente. No puedes volver a registrar el grupo.",
                    "nuevoRegistro", false
            ));
        }

        List<InvitacionEntity> invitadosConfirmados = new ArrayList<>();
        String codigoGrupo = null;

        if (asistira) {
            codigoGrupo = CodigoMatrimonioUnico.generarCodigo("MSM");
        }

        for (int i = 0; i < nombres.size(); i++) {
            String nombre = nombres.get(i).trim();
            if (nombre.isEmpty()) continue;

            Optional<InvitacionEntity> opt = invitationRepository.findByNombre(nombre);
            InvitacionEntity invitacion = opt.orElseGet(InvitacionEntity::new);

            // ✅ Esta validación ahora ya no se necesita porque ya validamos antes si alguien del grupo confirmó
            // if (opt.isPresent() && invitacion.getFechaConfirmacion() != null) {
            //     ZonedDateTime zoned = invitacion.getFechaConfirmacion()
            //             .atZone(ZoneId.of("GMT"))
            //             .withZoneSameInstant(ZoneId.of("America/Lima"));
            //     String fecha = zoned.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'a las' HH:mm"));
            //     return ResponseEntity.ok(Map.of(
            //             "mensaje", "⚠️ Ya registraste tu asistencia el " + fecha + ".",
            //             "nuevoRegistro", false
            //     ));
            // }

            invitacion.setNombre(nombre);
            invitacion.setAsistio(asistira);
            invitacion.setFecha(LocalDateTime.now());
            invitacion.setFechaConfirmacion(LocalDateTime.now());

            if (asistira) {
                invitacion.setCodigoMatrimonio(codigoGrupo);

                if (i == 0 && cantidadGrupo != null && cantidadGrupo > 1) {
                    invitacion.setEsRepresentante(true);
                    invitacion.setCantidadGrupo(cantidadGrupo);
                } else {
                    invitacion.setEsRepresentante(false);
                    invitacion.setCantidadGrupo(1);
                }
            } else {
                // Si no asistirá, dejamos el código como null
                invitacion.setCodigoMatrimonio(null);
                invitacion.setEsRepresentante(false);
                invitacion.setCantidadGrupo(1);
            }

            // 🔄 Guardamos
            invitationRepository.save(invitacion);
            invitadosConfirmados.add(invitacion);

            // 🔔 Notificar por WebSocket
            notificationService.notificarConfirmacion(invitacion);
            messagingTemplate.convertAndSend("/topic/invitaciones", invitacion);
        }

        // 🧾 Mensaje para el frontend
        String mensaje;
        if (asistira) {
            if (cantidadGrupo != null && cantidadGrupo > 1) {
                String apellidoRepresentante = nombres.get(0).split(" ")[nombres.get(0).split(" ").length - 1];
                mensaje = "🎉 Gracias por confirmar la asistencia de la familia " + apellidoRepresentante +
                        ".\n🎟️ Código de Familia: " + codigoGrupo;
            } else {
                mensaje = "🎉 Gracias por confirmar tu asistencia, " + nombres.get(0) +
                        ".\n🎟️ Tu código de confirmación es: " + codigoGrupo;
            }
        } else {
            mensaje = "💐 Gracias por avisarnos. Los tendremos presentes en nuestros corazones.";
        }

        // ✅ Evitamos el error usando HashMap (permite nulls)
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", mensaje);
        response.put("codigoMatrimonio", asistira ? codigoGrupo : null);
        response.put("nuevoRegistro", true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/marcar-entrada")
    public ResponseEntity<?> registrarEntradaFlexible(@RequestBody Map<String, String> body) {
        String codigo = body.get("codigoMatrimonio");
        String nombre = body.get("nombre");
        String modo = body.getOrDefault("modoEntrada", "manual");

        InvitacionEntity invitado = null;

        // 1. Buscar por código o nombre
        if (codigo != null && !codigo.isBlank()) {
            String codigoLimpio = codigo.trim().toUpperCase();
            if (!codigoLimpio.startsWith("MSM-")) {
                codigoLimpio = "MSM-" + codigoLimpio;
            }
            invitado = invitationRepository.findByCodigoMatrimonio(codigoLimpio)
                    .stream()
                    .filter(i -> Boolean.TRUE.equals(i.getEsRepresentante()))
                    .findFirst()
                    .orElse(null);
        }

        if (invitado == null && nombre != null && !nombre.isBlank()) {
            invitado = invitationRepository.findByNombre(nombre.trim()).orElse(null);
        }

        if (invitado == null) {
            return ResponseEntity.badRequest().body("❌ Invitado no encontrado.");
        }

        // 2. Obtener el grupo completo por codigo
        List<InvitacionEntity> grupo = invitationRepository.findByCodigoMatrimonio(invitado.getCodigoMatrimonio());

        // 3. Verificar si esta persona ya marcó entrada
        if (Boolean.TRUE.equals(invitado.getPresente())) {
            // Mostrar grupo completo, indicando quienes ya ingresaron y quienes faltan
            List<String> nombresYaIngresaron = grupo.stream()
                    .filter(i -> Boolean.TRUE.equals(i.getPresente()))
                    .map(InvitacionEntity::getNombre)
                    .toList();

            List<String> nombresFaltan = grupo.stream()
                    .filter(i -> !Boolean.TRUE.equals(i.getPresente()))
                    .map(InvitacionEntity::getNombre)
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "mensaje", "ℹ️ Esta persona ya marcó entrada previamente.",
                    "yaIngresaron", nombresYaIngresaron,
                    "faltanIngresar", nombresFaltan,
                    "codigoMatrimonio", invitado.getCodigoMatrimonio(),
                    "nombre", Objects.toString(invitado.getNombre(), ""),
                    "presente", true
            ));
        }

        // 4. Registrar entrada de esta persona
        ZonedDateTime nowLima = ZonedDateTime.now(ZoneId.of("America/Lima"));
        invitado.setPresente(true);
        invitado.setFechaEntrada(nowLima.toLocalDateTime());
        invitado.setModoEntrada(modo);
        invitationRepository.save(invitado);
        // 5. Preparar respuesta mostrando estado del grupo
        List<String> nombresYaIngresaron = grupo.stream()
                .filter(i -> Boolean.TRUE.equals(i.getPresente()))
                .map(InvitacionEntity::getNombre)
                .toList();

        List<String> nombresFaltan = grupo.stream()
                .filter(i -> !Boolean.TRUE.equals(i.getPresente()))
                .map(InvitacionEntity::getNombre)
                .toList();

        String mensaje = "✅ Entrada registrada para " + invitado.getNombre() + ".";
        if (!nombresFaltan.isEmpty()) {
            mensaje += "<br/><strong>Aún faltan por ingresar:</strong><ul>" +
                    nombresFaltan.stream().map(n -> "<li>👤 " + n + "</li>").collect(Collectors.joining()) +
                    "</ul>";
        }
        // Map de respuesta completo
        Map<String, Object> payload = Map.of(
                "mensaje", mensaje,
                "nombre", invitado.getNombre(),
                "codigoMatrimonio", invitado.getCodigoMatrimonio(),
                "modoEntrada", modo,
                "yaIngresaron", nombresYaIngresaron,
                "faltanIngresar", nombresFaltan,
                "presente", true
        );


        // 🚀 Notificar al WebSocket
        messagingTemplate.convertAndSend("/topic/invitaciones", payload);

        // ✅ Retornar al frontend
        return ResponseEntity.ok(payload);

    }

    @PostMapping("/buscar-grupo")
    public ResponseEntity<?> buscarGrupo(@RequestBody Map<String, String> body) {
        String codigoMatrimonio = body.getOrDefault("codigoMatrimonio", "").trim();
        String nombre = body.getOrDefault("nombre", "").trim();

        // 💡 Normaliza el código quitando letras, dejando solo números y anteponiendo MSM-
        if (!codigoMatrimonio.isBlank()) {
            String soloNumeros = codigoMatrimonio.replaceAll("[^0-9]", "");
            codigoMatrimonio = "MSM-" + soloNumeros;
        }

        List<InvitacionEntity> grupo = new ArrayList<>();

        if (!codigoMatrimonio.isBlank()) {
            grupo = invitationRepository.findByCodigoMatrimonio(codigoMatrimonio);
        } else if (!nombre.isBlank()) {
            // Buscar por nombre si no se dio código
            Optional<InvitacionEntity> invitado = invitationRepository.findByNombre(nombre);
            if (invitado.isPresent()) {
                codigoMatrimonio = invitado.get().getCodigoMatrimonio();
                grupo = invitationRepository.findByCodigoMatrimonio(codigoMatrimonio);
            }
        }

        if (grupo.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Invitado o grupo no encontrado.");
        }

        List<Map<String, Object>> integrantes = grupo.stream()
                .map(inv -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", inv.getId());
                    item.put("nombre", inv.getNombre());
                    item.put("presente", inv.getPresente());
                    return item;
                }).toList();

        return ResponseEntity.ok(Map.of(
                "codigoMatrimonio", codigoMatrimonio,
                "integrantes", integrantes
        ));
    }

    // ✅ Marcar entrada por ID
    @PostMapping("/marcar-entrada-por-id")
    public ResponseEntity<?> marcarEntradaPorId(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        String modo = body.getOrDefault("modoEntrada", "manual");

        Optional<InvitacionEntity> op = invitationRepository.findById(id);
        if (op.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Invitado no encontrado.");
        }

        InvitacionEntity invitado = op.get();

        if (Boolean.TRUE.equals(invitado.getPresente())) {
            return ResponseEntity.ok(Map.of(
                    "mensaje", "ℹ️ Esta persona ya ingresó.",
                    "nombre", invitado.getNombre(),
                    "codigoMatrimonio", invitado.getCodigoMatrimonio(),
                    "presente", true
            ));
        }
        // 🔥 👉 AQUI AGREGA ESTA LÓGICA:
        if (Boolean.FALSE.equals(invitado.getAsistio())) {
            System.out.println("🟡 Invitado dijo que NO asistiría pero se presentó: " + invitado.getNombre());
            invitado.setAsistio(true); // ✅ Marca que sí asistió finalmente
        }

        invitado.setPresente(true);
        invitado.setModoEntrada(modo);
        invitado.setFechaEntrada(ZonedDateTime.now(ZoneId.of("America/Lima")).toLocalDateTime());
        invitationRepository.save(invitado);

        List<InvitacionEntity> grupo = invitationRepository.findByCodigoMatrimonio(invitado.getCodigoMatrimonio());

        List<String> yaIngresaron = grupo.stream()
                .filter(i -> Boolean.TRUE.equals(i.getPresente()))
                .map(InvitacionEntity::getNombre)
                .toList();

        List<String> faltanIngresar = grupo.stream()
                .filter(i -> !Boolean.TRUE.equals(i.getPresente()))
                .map(InvitacionEntity::getNombre)
                .toList();

        Map<String, Object> payload = new HashMap<>();
        payload.put("mensaje", "✅ Entrada registrada para " + invitado.getNombre());
        payload.put("id", invitado.getId());
        payload.put("nombre", invitado.getNombre());
        payload.put("codigoMatrimonio", invitado.getCodigoMatrimonio());
        payload.put("yaIngresaron", yaIngresaron);
        payload.put("faltanIngresar", faltanIngresar);
        payload.put("presente", true);

        messagingTemplate.convertAndSend("/topic/invitaciones", payload);

        return ResponseEntity.ok(payload);
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