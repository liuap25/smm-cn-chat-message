package org.acme.chat.infraestructure.out.event;
import org.acme.shared.ChatMessage.ChatMessageResponseDto;
import org.acme.shared.ChatSidebar.ChatSidebarDTO;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import io.smallrye.reactive.messaging.annotations.Broadcast;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ChatMessagePublisher {

   // Canal universal para mensajes (lo usan ambos roles)
    @Inject
    @Channel("chat-messages-out")
    @Broadcast
    Emitter<ChatMessageResponseDto> emitter;

    // --------- Canales de Psychologist ----------
    @Inject
    @Channel("sidebar-psychologist-all-out")
    @Broadcast
    private Emitter<ChatSidebarDTO> psychologistAllEmitter;

    @Inject
    @Channel("sidebar-psychologist-unread-out")
    @Broadcast
    private Emitter<ChatSidebarDTO> psychologistUnreadEmitter;

    // --------- Canales de Patient ----------
    @Inject
    @Channel("sidebar-patient-all-out")
    @Broadcast
    private Emitter<ChatSidebarDTO> patientAllEmitter;

    @Inject
    @Channel("sidebar-patient-unread-out")
    @Broadcast
    private Emitter<ChatSidebarDTO> patientUnreadEmitter;

    // Publicar un mensaje
    public void publish(ChatMessageResponseDto messageDto) {
        System.out.println("📤 Publicando mensaje en RabbitMQ: " + messageDto);
        emitter.send(messageDto);
    }

    // Publicar actualizaciones de sidebar según rol
    public void publishPsychologistSidebar(ChatSidebarDTO dto) {
        System.out.println("📤 Publicando actualización de sidebar (Psychologist): " + dto);
        psychologistAllEmitter.send(dto);
    }

    public void publishPatientSidebar(ChatSidebarDTO dto) {
        System.out.println("📤 Publicando actualización de sidebar (Patient): " + dto);
        patientAllEmitter.send(dto);
    }

    // ---------- NUEVOS MÉTODOS PARA UNREAD ----------
    public void publishPsychologistSidebarUnread(ChatSidebarDTO dto) {
        System.out.println("📤 Publicando actualización UNREAD (Psychologist): " + dto);
        psychologistUnreadEmitter.send(dto);
    }

    public void publishPatientSidebarUnread(ChatSidebarDTO dto) {
        System.out.println("📤 Publicando actualización UNREAD (Patient): " + dto);
        patientUnreadEmitter.send(dto);
    }

        
}
