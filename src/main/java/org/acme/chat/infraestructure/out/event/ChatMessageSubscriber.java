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

      // Multi<JsonObject> que recibe los mensajes entrantes del canal
    @Channel("chat-messages-in")
    Multi<JsonObject> messages;

    // Consumir mensajes del canal y enviar a WebSocket
    @Incoming("chat-messages-in")
    @Outgoing("websocket-broadcast")
    public ChatMessageResponseDto consume(JsonObject json) {
        ChatMessageResponseDto dto = mapJsonToDto(json);
        System.out.println("📩 Mensaje DTO convertido: " + dto);
        return dto;
    }

    // Suscripción para que el frontend reciba los mensajes en tiempo real
    public Multi<ChatMessageResponseDto> subscribe() {
        return messages.map(this::mapJsonToDto);
    }

    // Método auxiliar para mapear JsonObject a DTO incluyendo readAt y sentAt
    private ChatMessageResponseDto mapJsonToDto(JsonObject json) {
        ChatMessageResponseDto dto = new ChatMessageResponseDto();
        dto.setId(UUID.fromString(json.getString("id")));
        dto.setChatGroupId(UUID.fromString(json.getString("chatGroupId")));
        dto.setMessage(json.getString("message"));

        // Parsear sentAt si existe
        if (json.containsKey("sentAt") && json.getString("sentAt") != null) {
            dto.setSentAt(Instant.parse(json.getString("sentAt")));
        }

        // Parsear readAt si existe
        if (json.containsKey("readAt") && json.getString("readAt") != null) {
            dto.setReadAt(Instant.parse(json.getString("readAt")));
        }


        return dto;
    }
}
