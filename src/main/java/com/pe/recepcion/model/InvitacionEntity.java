package com.pe.recepcion.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "invitations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitacionEntity {
    @Id
    private String id;
    private String nombre;
    private String correo;
    private boolean confirmado = false; // Confirmó su asistencia
    private boolean asistio = false;    // Marcado en la puerta
    private boolean accesoEspecial = false; // ← mejor que "esAdmin"
    private LocalDateTime fecha = LocalDateTime.now();
    private String codigoQR;
    private LocalDateTime expiracionQR;
    private String evento;
    private String tipoInvitacion; // Ejemplo: "NORMAL, VIP, PONENTE, etc.
    private String creadoPorUsuarioId; // ID del usuario que la creó

}
