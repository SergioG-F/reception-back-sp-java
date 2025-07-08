package com.pe.recepcion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AuthResponse {
        public AuthResponse(String token, String correo, String nombreCompleto, String username, String rol) {
                this.token = token;
                this.correo = correo;
                this.nombreCompleto = nombreCompleto;
                this.username = username;
                this.rol = rol;
        }

        private String token;
        private String correo;
        private String nombreCompleto;
        private String username;
        private String rol;

        public String getToken() {
                return token;
        }

        public void setToken(String token) {
                this.token = token;
        }

        public String getCorreo() {
                return correo;
        }

        public void setCorreo(String correo) {
                this.correo = correo;
        }

        public String getNombreCompleto() {
                return nombreCompleto;
        }

        public void setNombreCompleto(String nombreCompleto) {
                this.nombreCompleto = nombreCompleto;
        }

        public String getUsername() {
                return username;
        }

        public void setUsername(String username) {
                this.username = username;
        }

        public String getRol() {
                return rol;
        }

        public void setRol(String rol) {
                this.rol = rol;
        }
}
