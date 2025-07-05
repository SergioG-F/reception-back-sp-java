
package com.pe.recepcion.repository;

import com.pe.recepcion.model.UsuarioEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UsuarioEntity,String> {
    Optional<UsuarioEntity> findByCorreo(String correo);
    Optional<UsuarioEntity> findByCorreoOrUserName(String correo, String userName);

    Optional<UsuarioEntity> findByUserName(String userName);

}
