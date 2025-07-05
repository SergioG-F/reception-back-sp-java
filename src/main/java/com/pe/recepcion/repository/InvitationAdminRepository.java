package com.pe.recepcion.repository;

import com.pe.recepcion.model.InvitacionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InvitationAdminRepository extends MongoRepository<InvitacionEntity,String> {
}
