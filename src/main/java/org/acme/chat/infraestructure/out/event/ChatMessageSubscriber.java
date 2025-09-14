package org.acme.chat.infraestructure.out.event;
import java.time.Instant;
import java.util.UUID;

import org.acme.shared.ChatMessage.ChatMessageResponseDto;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.mutiny.Multi;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ChatMessageSubscriber {


    @Channel("chat-messages-in")
    Multi<JsonObject> messages;

    
    @Incoming("chat-messages-in")
    @Outgoing("websocket-broadcast")
    public ChatMessageResponseDto consume(JsonObject json) {
        ChatMessageResponseDto dto = mapJsonToDto(json);
        System.out.println("📩 Mensaje DTO convertido: " + dto);
        return dto;
    }

    public Multi<ChatMessageResponseDto> subscribe() {
        return messages.map(this::mapJsonToDto);
    }

    private ChatMessageResponseDto mapJsonToDto(JsonObject json) {
        ChatMessageResponseDto dto = new ChatMessageResponseDto();
        dto.setId(UUID.fromString(json.getString("id")));
        dto.setChatGroupId(UUID.fromString(json.getString("chatGroupId")));
        dto.setMessage(json.getString("message"));

        if (json.containsKey("sentAt") && json.getString("sentAt") != null) {
            dto.setSentAt(Instant.parse(json.getString("sentAt")));
        }
    
        if (json.containsKey("readAt") && json.getString("readAt") != null) {
            dto.setReadAt(Instant.parse(json.getString("readAt")));
        }

        if (json.containsKey("senderID") && json.getString("senderID") != null) {
        dto.setSenderID(json.getString("senderID"));
        }


        return dto;
    }
}
