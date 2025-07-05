package com.pe.recepcion.mock;

import com.pe.recepcion.model.InvitacionEntity;
import com.pe.recepcion.model.Rol;
import com.pe.recepcion.model.UsuarioEntity;
import com.pe.recepcion.repository.InvitationAdminRepository;
import com.pe.recepcion.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final InvitationAdminRepository invitacionRepo;


    @Override
    public void run(String... args) {
        if (userRepo.findByCorreo("admin@gmail.com").isEmpty()) {
            UsuarioEntity admin = new UsuarioEntity();
            admin.setNombreCompleto("Sergio Guzman Fernandez");
            admin.setCorreo("admin@gmail.com");
            admin.setUserName("admin");
            admin.setPassword(passwordEncoder.encode("admin123")); // ⚠️ Contraseña segura
            admin.setRol(Rol.ADMIN);
            admin.setActivo(true);
            userRepo.save(admin);
            System.out.println("🔐 Usuario admin creado.");
        }
        if (userRepo.findByCorreo("recepcion@gmail.com").isEmpty()) {
            UsuarioEntity reception = new UsuarioEntity();
            reception.setNombreCompleto("George Lucas Smith");
            reception.setCorreo("recepcion@gmail.com");
            reception.setUserName("recepcion");
            reception.setPassword(passwordEncoder.encode("recepcion123456789")); // ⚠️ Contraseña segura
            reception.setRol(Rol.RECEPCIONISTA);
            reception.setActivo(true);
            userRepo.save(reception);
            System.out.println("🔐 Usuario Recepcionista creado.");
        }
        if (invitacionRepo.count() == 0) {
            InvitacionEntity invitado1 = new InvitacionEntity();
            invitado1.setNombre("Juan Pérez");
            invitado1.setCorreo("juan@example.com");
            invitado1.setConfirmado(true);
            invitado1.setAsistio(false);
            invitado1.setAccesoEspecial(false);
            invitado1.setEvento("Conferencia 2025");
            invitado1.setTipoInvitacion("NORMAL");
            invitado1.setCreadoPorUsuarioId("admin");

            InvitacionEntity invitado2 = new InvitacionEntity();
            invitado2.setNombre("María Gómez");
            invitado2.setCorreo("maria@example.com");
            invitado2.setConfirmado(false);
            invitado2.setAsistio(false);
            invitado2.setAccesoEspecial(true);
            invitado2.setEvento("Conferencia 2025");
            invitado2.setTipoInvitacion("VIP");
            invitado2.setCreadoPorUsuarioId("admin");

            InvitacionEntity invitado3 = new InvitacionEntity();
            invitado3.setNombre("maria perez López");
            invitado3.setCorreo("carlos@example.com");
            invitado3.setConfirmado(true);
            invitado3.setAsistio(true);
            invitado3.setAccesoEspecial(false);
            invitado3.setEvento("Conferencia 2025");
            invitado3.setTipoInvitacion("PONENTE");
            invitado3.setCreadoPorUsuarioId("admin");
            InvitacionEntity invitado4 = new InvitacionEntity();
            invitado4.setNombre("Carlos López");
            invitado4.setCorreo("carlos@example.com");
            invitado4.setConfirmado(true);
            invitado4.setAsistio(true);
            invitado4.setAccesoEspecial(false);
            invitado4.setEvento("Conferencia 2025");
            invitado4.setTipoInvitacion("PONENTE");
            invitado4.setCreadoPorUsuarioId("admin");

            invitacionRepo.saveAll(List.of(invitado1, invitado2, invitado3));
            System.out.println("📋 Invitaciones de prueba creadas.");
        }
    }
}
