package com.pe.recepcion.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "invitations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class InvitacionEntity {
    @Id
    private String id;
    private String nombre;
    private String correo;
    @Builder.Default
    private boolean confirmado = false; // Confirmó su asistencia
    @Builder.Default
    private boolean accesoEspecial = false; // ← mejor que "esAdmin"
    private Boolean asistio;    // confirmó que asistirá (semanas antes)
    private Boolean presente;    // presencia real el día del evento
    private LocalDateTime fechaConfirmacion;
    private LocalDateTime fechaEntrada;
    @Builder.Default
    private LocalDateTime fecha = LocalDateTime.now();
    @Indexed(unique = true, sparse = true)
    private String codigoMatrimonio; // Código de Matrimonio (si aplica)
    private LocalDateTime expiracionQR;
    private String evento;
    private String tipoInvitacion; // Ejemplo: "NORMAL, VIP, PONENTE, etc.
    private String creadoPorUsuarioId; // ID del usuario que la creó
    private String modoEntrada; // "qr" o "manual"

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
    }

    public boolean isAccesoEspecial() {
        return accesoEspecial;
    }

    public void setAccesoEspecial(boolean accesoEspecial) {
        this.accesoEspecial = accesoEspecial;
    }

    public Boolean getAsistio() {
        return asistio;
    }

    public void setAsistio(Boolean asistio) {
        this.asistio = asistio;
    }

    public Boolean getPresente() {
        return presente;
    }

    public void setPresente(Boolean presente) {
        this.presente = presente;
    }

    public LocalDateTime getFechaConfirmacion() {
        return fechaConfirmacion;
    }

    public void setFechaConfirmacion(LocalDateTime fechaConfirmacion) {
        this.fechaConfirmacion = fechaConfirmacion;
    }

    public LocalDateTime getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(LocalDateTime fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getCodigoMatrimonio() {
        return codigoMatrimonio;
    }

    public void setCodigoMatrimonio(String codigoMatrimonio) {
        this.codigoMatrimonio = codigoMatrimonio;
    }

    public LocalDateTime getExpiracionQR() {
        return expiracionQR;
    }

    public void setExpiracionQR(LocalDateTime expiracionQR) {
        this.expiracionQR = expiracionQR;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getTipoInvitacion() {
        return tipoInvitacion;
    }

    public void setTipoInvitacion(String tipoInvitacion) {
        this.tipoInvitacion = tipoInvitacion;
    }

    public String getCreadoPorUsuarioId() {
        return creadoPorUsuarioId;
    }

    public void setCreadoPorUsuarioId(String creadoPorUsuarioId) {
        this.creadoPorUsuarioId = creadoPorUsuarioId;
    }

    public String getModoEntrada() {
        return modoEntrada;
    }

    public void setModoEntrada(String modoEntrada) {
        this.modoEntrada = modoEntrada;
    }
    }
