package com.pe.recepcion.service;

import com.pe.recepcion.model.InvitacionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class WsInvitationService {
    private final SimpMessagingTemplate template;

    public void notificarConfirmacion(InvitacionEntity inv) {
        log.info("📢 Enviando notificación de asistencia vía WebSocket para: {}", inv.getNombre());
        template.convertAndSend("/topic/invitaciones", inv);
    }
    public void notificarEliminacion(String id) {
        log.info("📢 Enviando notificación de ELIMINACIÓN vía WebSocket para ID: {}", id);
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", id);
        payload.put("eliminado", true);
        template.convertAndSend("/topic/invitaciones", payload);
    }
}
