package com.pe.recepcion.service;

import com.pe.recepcion.model.InvitacionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class WsInvitationService {
    private final SimpMessagingTemplate template;

    public void notificarConfirmacion(InvitacionEntity inv) {
        log.info("📢 Enviando notificación de asistencia vía WebSocket para: {}", inv.getNombre());
        template.convertAndSend("/topic/invitaciones", inv);
    }
}
