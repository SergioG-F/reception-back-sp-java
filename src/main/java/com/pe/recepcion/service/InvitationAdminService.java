package com.pe.recepcion.service;

import com.pe.recepcion.model.InvitacionEntity;
import com.pe.recepcion.model.UsuarioEntity;
import com.pe.recepcion.repository.InvitationAdminRepository;
import com.pe.recepcion.repository.InvitationRepository;
import com.pe.recepcion.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class InvitationAdminService {

    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final InvitationAdminRepository repo;


    public List<UsuarioEntity> listarUsers() {
        return userRepository.findAll();
    }

    public List<InvitacionEntity> listInvitations() {
        return invitationRepository.findAll();
    }

    public Optional<InvitacionEntity> buscarPorId(String id) {
        return invitationRepository.findById(id);
    }

    public InvitacionEntity guardar(InvitacionEntity i) {
        return invitationRepository.save(i);
    }


    public void eliminarUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrada");
        }
        userRepository.deleteById(id);
    }

    public void eliminarGuests(String id) {
        if (!invitationRepository.existsById(id)) {
            throw new RuntimeException("Invitación no encontrada");
        }
        invitationRepository.deleteById(id);
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
