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

    @Inject
    @Channel("chat-messages-out") 
    @Broadcast
    Emitter<ChatMessageResponseDto> emitter;


    @Inject
    @Channel("chat-sidebar-update") 
    @Broadcast
    Emitter<ChatSidebarDTO> sidebarEmitter;

    public void publish(ChatMessageResponseDto messageDto) {
        System.out.println("📤 Publicando mensaje en RabbitMQ: " + messageDto);
        emitter.send(messageDto);
    }


     public void publishSidebarUpdate(ChatSidebarDTO sidebarDto) {
        System.out.println("📤 Publicando actualización de sidebar: " + sidebarDto);
        sidebarEmitter.send(sidebarDto);
    }



    



        
}
