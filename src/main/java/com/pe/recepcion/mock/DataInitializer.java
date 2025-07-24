package com.pe.recepcion.mock;

import com.pe.recepcion.model.InvitacionEntity;
import com.pe.recepcion.model.Rol;
import com.pe.recepcion.model.UsuarioEntity;
import com.pe.recepcion.repository.InvitationAdminRepository;
import com.pe.recepcion.repository.UserRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Getter
@Setter
@Data
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final InvitationAdminRepository invitacionRepo;

    public DataInitializer(UserRepository userRepo, PasswordEncoder passwordEncoder, InvitationAdminRepository invitacionRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.invitacionRepo = invitacionRepo;
    }

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
        /*
        if (invitacionRepo.count() == 0) {
            InvitacionEntity invitado1 = new InvitacionEntity();
            invitado1.setNombre("Juan Pérez");
            invitado1.setCorreo("juan@example.com");
            invitado1.setConfirmado(true);
            invitado1.setAsistio(false);
            invitado1.setFechaConfirmacion(LocalDateTime.now());
            invitado1.setAccesoEspecial(false);
            invitado1.setCodigoMatrimonio("MSM-1");
            invitado1.setEvento("Matrimonio Sergio & Massiel");
            invitado1.setTipoInvitacion("NORMAL");
            invitado1.setCreadoPorUsuarioId("admin");
            invitado1.setModoEntrada("qr");

            InvitacionEntity invitado2 = new InvitacionEntity();
            invitado2.setNombre("María Gómez");
            invitado2.setCorreo("maria@example.com");
            invitado2.setConfirmado(false);
            invitado2.setAsistio(false);
            invitado2.setFechaConfirmacion(LocalDateTime.now());
            invitado2.setAccesoEspecial(true);
            invitado2.setCodigoMatrimonio("MSM-2");
            invitado2.setEvento("Matrimonio Sergio & Massiel");
            invitado2.setTipoInvitacion("VIP");
            invitado2.setCreadoPorUsuarioId("admin");
            invitado2.setModoEntrada("manual");
            invitacionRepo.saveAll(List.of(invitado1, invitado2));
            System.out.println("📋 Invitaciones de prueba creadas.");
        }

         */
    }
}
