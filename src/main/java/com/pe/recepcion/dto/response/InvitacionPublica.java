package com.pe.recepcion.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class InvitacionPublica {
    private String nombre;
    private String evento;
    private boolean asistio;
    private LocalDateTime fecha;
}
