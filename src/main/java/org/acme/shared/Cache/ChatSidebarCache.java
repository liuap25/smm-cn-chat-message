package org.acme.shared.Cache;

import org.acme.shared.ChatSidebar.ChatSidebarDTO;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.value.ReactiveValueCommands;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ChatSidebarCache {

  private final ReactiveValueCommands<String, String> valueCommands;

    @Inject
    ObjectMapper objectMapper;

    private static final String PREFIX = "chat:sidebar:";
    
    @Inject
    public ChatSidebarCache(ReactiveRedisDataSource ds) {
        this.valueCommands = ds.value(String.class);
    }

    public Uni<Void> save(ChatSidebarDTO sidebar) {
        String key = PREFIX + sidebar.getChatGroupId() + ":" + sidebar.getOtherUserId();
        
        return Uni.createFrom().item(() -> {
            try {
                return objectMapper.writeValueAsString(sidebar);
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize ChatSidebarDTO", e);
            }
        })
        .onItem().transformToUni(value -> valueCommands.set(key, value));
    }

    public Uni<ChatSidebarDTO> get(String chatGroupId, String otherUserId) {
        String key = PREFIX + chatGroupId + ":" + otherUserId;
        
        return valueCommands.get(key)
                 .onItem().transform(value -> {
                     if (value == null) {
                         return null;
                     }
                     try {
                         return objectMapper.readValue(value, ChatSidebarDTO.class);
                     } catch (Exception e) {
                         // Puedes manejar el fallo aquí si es necesario
                         return null;
                     }
                 });
    }

    // Este método ahora actualiza el unreadCount y devuelve el DTO completo
    public Uni<ChatSidebarDTO> updateAndGetSidebar(String chatGroupId, String otherUserId, long unreadCount) {
        return get(chatGroupId, otherUserId)
            .onItem().transformToUni(sidebar -> {
                if (sidebar == null) {
                    return Uni.createFrom().nullItem();
                }
                sidebar.setUnreadCount(unreadCount);
                // Guardar y luego devolver el mismo DTO actualizado
                return save(sidebar).replaceWith(sidebar);
            });
    }
}
