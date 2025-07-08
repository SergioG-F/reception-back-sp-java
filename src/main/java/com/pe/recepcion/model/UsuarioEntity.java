package com.pe.recepcion.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UsuarioEntity {
    @Id
    private String id;

    private String nombreCompleto;

    @Indexed(unique = true)
    private String correo;

    private String password;
    private String userName;

    private Rol rol;

    private List<String> invitacionesAsociadas; // Opcional: lista de IDs de invitaciones

    private boolean activo = true;

    public static UsuarioEntityBuilder builder() {
        return new UsuarioEntityBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public List<String> getInvitacionesAsociadas() {
        return invitacionesAsociadas;
    }

    public void setInvitacionesAsociadas(List<String> invitacionesAsociadas) {
        this.invitacionesAsociadas = invitacionesAsociadas;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public static class UsuarioEntityBuilder {
        private String id;
        private String nombreCompleto;
        private String correo;
        private String password;
        private String userName;
        private Rol rol;
        private List<String> invitacionesAsociadas;
        private boolean activo;

        UsuarioEntityBuilder() {
        }

        public UsuarioEntityBuilder id(String id) {
            this.id = id;
            return this;
        }

        public UsuarioEntityBuilder nombreCompleto(String nombreCompleto) {
            this.nombreCompleto = nombreCompleto;
            return this;
        }

        public UsuarioEntityBuilder correo(String correo) {
            this.correo = correo;
            return this;
        }

        public UsuarioEntityBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UsuarioEntityBuilder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public UsuarioEntityBuilder rol(Rol rol) {
            this.rol = rol;
            return this;
        }

        public UsuarioEntityBuilder invitacionesAsociadas(List<String> invitacionesAsociadas) {
            this.invitacionesAsociadas = invitacionesAsociadas;
            return this;
        }

        public UsuarioEntityBuilder activo(boolean activo) {
            this.activo = activo;
            return this;
        }


        public String toString() {
            return "UsuarioEntity.UsuarioEntityBuilder(id=" + this.id + ", nombreCompleto=" + this.nombreCompleto + ", correo=" + this.correo + ", password=" + this.password + ", userName=" + this.userName + ", rol=" + this.rol + ", invitacionesAsociadas=" + this.invitacionesAsociadas + ", activo=" + this.activo + ")";
        }
    }
}
