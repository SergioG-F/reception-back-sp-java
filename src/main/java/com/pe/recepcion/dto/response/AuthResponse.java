package com.pe.recepcion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

        private String token;
        private String correo;
        private String nombreCompleto;
        private String username;
        private String rol;

}
