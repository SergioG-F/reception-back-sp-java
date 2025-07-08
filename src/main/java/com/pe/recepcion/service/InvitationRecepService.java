package com.pe.recepcion.service;

import com.pe.recepcion.model.InvitacionEntity;
import com.pe.recepcion.repository.InvitationAdminRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Getter
@Setter
@Data
public class InvitationRecepService {

    private final InvitationAdminRepository repo;
    private final SimpMessagingTemplate messagingTemplate; // ✅ Para WebSocket

    public InvitationRecepService(InvitationAdminRepository repo, SimpMessagingTemplate messagingTemplate) {
        this.repo = repo;
        this.messagingTemplate = messagingTemplate;
    }

    public List<InvitacionEntity> listar() {
        return repo.findAll();
    }

    public InvitacionEntity marcarAsistencia(String id) {
        InvitacionEntity invitacion = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Invitación no encontrada"));

        if (invitacion.isConfirmado()) {
            throw new RuntimeException("Asistencia ya fue registrada.");
        }
        invitacion.setAsistio(true);
        InvitacionEntity actualizada = repo.save(invitacion);
        // ✅ Emitir actualización por WebSocket
        messagingTemplate.convertAndSend("/topic/invitaciones", actualizada);

        return actualizada;
    }

}
