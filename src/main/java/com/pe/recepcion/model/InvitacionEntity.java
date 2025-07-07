package com.pe.recepcion.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
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
    private boolean accesoEspecial = false; // ← mejor que "esAdmin"
    private Boolean asistio;    // confirmó que asistirá (semanas antes)
    private Boolean presente;    // presencia real el día del evento
    private LocalDateTime fechaConfirmacion;
    private LocalDateTime fechaEntrada;
    private LocalDateTime fecha = LocalDateTime.now();
    @Indexed(unique = true, sparse = true)
    private String codigoMatrimonio; // Código de Matrimonio (si aplica)
    private LocalDateTime expiracionQR;
    private String evento;
    private String tipoInvitacion; // Ejemplo: "NORMAL, VIP, PONENTE, etc.
    private String creadoPorUsuarioId; // ID del usuario que la creó
    private String modoEntrada; // "qr" o "manual"

}
