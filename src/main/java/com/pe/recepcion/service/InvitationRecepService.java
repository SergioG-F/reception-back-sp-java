package com.pe.recepcion.service;

import com.pe.recepcion.model.InvitacionEntity;
import com.pe.recepcion.repository.InvitationAdminRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class InvitationRecepService {

    private final InvitationAdminRepository repo;

    public List<InvitacionEntity> listar() {
        return repo.findAll();
    }

    public Optional<InvitacionEntity> buscarPorId(String id) {
        return repo.findById(id);
    }

    public InvitacionEntity marcarAsistencia(String id) {
        InvitacionEntity invitacion = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Invitación no encontrada"));

        if (invitacion.isAsistio()) {
            throw new RuntimeException("Asistencia ya fue registrada.");
        }

        invitacion.setAsistio(true);
        return repo.save(invitacion);
    }

}
