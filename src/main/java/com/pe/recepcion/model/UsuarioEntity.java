package com.pe.recepcion.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
