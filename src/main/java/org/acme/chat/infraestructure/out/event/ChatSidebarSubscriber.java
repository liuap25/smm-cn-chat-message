package org.acme.chat.infraestructure.out.event;

import java.time.Instant;

import org.acme.shared.ChatSidebar.ChatSidebarDTO;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.smallrye.mutiny.Multi;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ChatSidebarSubscriber {

     // Recibe mensajes crudos como JsonObject desde RabbitMQ
    @Channel("chat-sidebar-update-in")
    Multi<JsonObject> sidebarUpdates;

    // Procesa cada mensaje entrante
    @Incoming("chat-sidebar-update-in")
    public void consume(JsonObject json) {
        ChatSidebarDTO dto = mapJsonToDto(json);
        System.out.println("📩 Sidebar DTO recibido: " + dto);
    }

    // Devuelve un Multi<ChatSidebarDTO> listo para GraphQL Subscription
    public Multi<ChatSidebarDTO> getSidebarUpdates() {
        return sidebarUpdates.map(this::mapJsonToDto);
    }

    // Mapea JsonObject -> ChatSidebarDTO
    private ChatSidebarDTO mapJsonToDto(JsonObject json) {
        return new ChatSidebarDTO(
            json.getString("chatGroupId"),
            json.getString("otherUserId"),
            json.getString("fullName"),
            json.getString("photoUrl"),
            json.getString("lastMessage"),
            json.containsKey("lastMessageDate") && json.getString("lastMessageDate") != null
                ? Instant.parse(json.getString("lastMessageDate"))
                : null,
            json.containsKey("unreadCount") && json.getValue("unreadCount") != null
                ? json.getLong("unreadCount")
                : 0L
        );
    }
    
}
