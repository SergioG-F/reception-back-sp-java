package com.pe.recepcion.repository;

import com.pe.recepcion.model.InvitacionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface InvitationRepository extends MongoRepository<InvitacionEntity,String> {
    Optional<InvitacionEntity> findByNombre(String nombre);
    Optional<InvitacionEntity> findByCodigoMatrimonio(String codigo);

}
