package com.pe.recepcion.controller;

import com.pe.recepcion.model.InvitacionEntity;
import com.pe.recepcion.model.Rol;
import com.pe.recepcion.model.UsuarioEntity;
import com.pe.recepcion.repository.InvitationRepository;
import com.pe.recepcion.repository.UserRepository;
import com.pe.recepcion.service.GenerationQrService;
import com.pe.recepcion.service.InvitationAdminService;
import com.pe.recepcion.service.WsInvitationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/invitaciones")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Log4j2
@Getter
@Setter
@Data
public class AdminController {
    // Total control: crear, editar, eliminar, listar, etc.

    public AdminController(InvitationAdminService servicio, UserRepository userRepo, InvitationRepository invitationRepository, GenerationQrService generarQRGeneral, PasswordEncoder passwordEncoder, WsInvitationService wsService) {
        this.servicio = servicio;
        this.userRepo = userRepo;
        this.invitationRepository = invitationRepository;
        this.generarQRGeneral = generarQRGeneral;
        this.passwordEncoder = passwordEncoder;
        this.wsService = wsService;
    }

    private final InvitationAdminService servicio;
    private final UserRepository userRepo;
    private final InvitationRepository invitationRepository;
    private final GenerationQrService generarQRGeneral;
    private final PasswordEncoder passwordEncoder;
    private final WsInvitationService wsService;



    // 🔹 Obtener todas las invitaciones
    @GetMapping("/users")
    public List<UsuarioEntity> listUsers() {
        return servicio.listarUsers();
    }

    @GetMapping("/invitados")
    public List<InvitacionEntity> listInvitados() {
        return servicio.listInvitations();
    }

    // 🔹 Obtener una invitación por ID
    @GetMapping("/{id}")
    public ResponseEntity<InvitacionEntity> obtenerPorId(@PathVariable String id) {
        return servicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Crear ADMINISTRADOR o RECEPCIONISTA  ()
    @PostMapping("/crear/user")
    public ResponseEntity<?> crearUsuario(@RequestBody UsuarioEntity newUser) {
        if ((newUser.getCorreo() == null || newUser.getCorreo().isBlank()) &&
                (newUser.getUserName() == null || newUser.getUserName().isBlank())) {
            return ResponseEntity.badRequest().body("Se requiere correo o username.");
        }

        if (newUser.getPassword() == null || newUser.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("La contraseña no puede estar vacía.");
        }

        // Validar existencia por correo o username
        if (newUser.getCorreo() != null && userRepo.findByCorreo(newUser.getCorreo()).isPresent()) {
            return ResponseEntity.badRequest().body("Ya existe un usuario con ese correo.");
        }

        if (newUser.getUserName() != null && userRepo.findByUserName(newUser.getUserName()).isPresent()) {
            return ResponseEntity.badRequest().body("Ya existe un usuario con ese username.");
        }

        // Encriptar contraseña
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setActivo(true);

        UsuarioEntity guardado = userRepo.save(newUser);
        return ResponseEntity.ok(guardado);
    }

    // 🔹 Crear INVITADOS
    @PostMapping("/crear/guests")
    public ResponseEntity<?> crearInvitado(@RequestBody Map<String, String> datos, Principal p) {
        String nombre = datos.get("nombre");

        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre es obligatorio.");
        }

        String evento = datos.getOrDefault("evento", "Boda Sergio & Massiel");
        String tipoInvitacion = datos.getOrDefault("tipoInvitacion", "NORMAL");
        String fecha = datos.getOrDefault("fecha", String.valueOf(LocalDateTime.now()));

        // Obtener ID del usuario que está creando (recepcionista)
        String correo = p.getName();
        UsuarioEntity creador = userRepo.findByCorreoOrUserName(correo, correo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "El usuario con correo " + correo + " no está registrado en la BD"
                ));

        // Crear la invitación con datos mínimos

        InvitacionEntity i =new InvitacionEntity();
                i.setNombre(nombre);
                i.setEvento(evento);
                i.setTipoInvitacion(tipoInvitacion);
                i.setCreadoPorUsuarioId(creador.getId());
                i.setFecha(LocalDateTime.parse(fecha));


        try {
            InvitacionEntity guardado = servicio.guardar(i);
            wsService.notificarConfirmacion(guardado); // 🔴 Agrega esto
            return ResponseEntity.ok(guardado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al generar el código QR.");
        }
    }

    // 🔹 Actualizar una User
    @PutMapping("/actualizar/user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Solo ADMIN puede actualizar usuarios
    public ResponseEntity<?> actualizarUsuario(@PathVariable String id, @RequestBody UsuarioEntity nuevo, Principal principal) {

        Optional<UsuarioEntity> optAuthUser = userRepo.findByCorreoOrUserName(principal.getName(), principal.getName());
        if (optAuthUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario autenticado no válido.");
        }
        UsuarioEntity authUser = optAuthUser.get();
        return userRepo.findById(id).map(usuario -> {
            if (nuevo.getNombreCompleto() != null) usuario.setNombreCompleto(nuevo.getNombreCompleto());
            if (nuevo.getCorreo() != null) usuario.setCorreo(nuevo.getCorreo());
            if (nuevo.getUserName() != null) usuario.setUserName(nuevo.getUserName());
            if (nuevo.getRol() != null) usuario.setRol(nuevo.getRol());
            if (nuevo.getPassword() != null && !nuevo.getPassword().isBlank()) {
                usuario.setPassword(passwordEncoder.encode(nuevo.getPassword()));
            }
            // Solo el ADMIN puede modificar el campo 'activo'
            if (authUser.getRol() == Rol.ADMIN && nuevo.isActivo() != usuario.isActivo()) {
                usuario.setActivo(nuevo.isActivo());
            }
            return ResponseEntity.ok(userRepo.save(usuario));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Actualizar una Guest
    @PutMapping("/actualizar/guest/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Solo ADMIN puede actualizar Guests
    public ResponseEntity<?> actualizarInvitado(@PathVariable String id, @RequestBody Map<String, Object> datos) {
        return invitationRepository.findById(id).map(i -> {
            if (datos.containsKey("nombre")) i.setNombre((String) datos.get("nombre"));
            if (datos.containsKey("confirmado")) i.setConfirmado((Boolean) datos.get("confirmado"));
            if (datos.containsKey("asistio")) i.setAsistio((Boolean) datos.get("asistio"));
            if (datos.containsKey("presente")) i.setPresente((Boolean) datos.get("presente")); // ✅ ESTA LÍNEA ES CLAVE
            if (datos.containsKey("presente")) {
                        Boolean nuevoValor = (Boolean) datos.get("presente");
                        i.setPresente(nuevoValor);
                        if (nuevoValor != null && nuevoValor && i.getFechaEntrada() == null) {
                            i.setFechaEntrada(LocalDateTime.now()); // Solo registra la primera vez que se marca como presente
                        }
                    }
             InvitacionEntity actualizado = invitationRepository.save(i);
            wsService.notificarConfirmacion(actualizado); // 🔴 Agrega esto
            return ResponseEntity.ok(actualizado);
        }).orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Eliminar un User
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        try {
            servicio.eliminarUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔹 Eliminar una invitación
    @DeleteMapping("/guest/{id}")
    public ResponseEntity<Void> deleteGuests(@PathVariable String id) {
        Optional<InvitacionEntity> invitado = invitationRepository.findById(id);
        try {
            if (invitado.isPresent()) {
                wsService.notificarEliminacion(id); // 🔴 NUEVO
                servicio.eliminarGuests(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    //Generar QR general
    @PostMapping("/qr-general")
    public ResponseEntity<String> generarQRGeneral() {
        try {
            generarQRGeneral.generarQRGeneral(); // genera el archivo
            return ResponseEntity.ok("✅ Código QR general generado con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error al generar el QR.");
        }
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

}