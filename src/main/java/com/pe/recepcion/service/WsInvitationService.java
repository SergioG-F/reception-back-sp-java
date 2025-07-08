package com.pe.recepcion.service;

import com.pe.recepcion.model.InvitacionEntity;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
@Getter
@Setter
@Data
public class WsInvitationService {
    private final SimpMessagingTemplate template;

    public WsInvitationService(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void notificarConfirmacion(InvitacionEntity inv) {

        template.convertAndSend("/topic/invitaciones", inv);
    }
    public void notificarEliminacion(String id) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", id);
        payload.put("eliminado", true);
        template.convertAndSend("/topic/invitaciones", payload);
    }
}
