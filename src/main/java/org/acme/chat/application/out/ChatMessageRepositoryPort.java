package org.acme.chat.application.out;

import java.time.Instant;
import java.util.List;


import org.acme.chat.domain.model.ChatMessage;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public interface ChatMessageRepositoryPort {
    Uni<ChatMessage> save(ChatMessage message);
    Multi<ChatMessage> findByChatGroupId(String chatGroupId);
    Uni<List<ChatMessage>> findAllByChatGroupId(String chatGroupId,int offset,int limit);

    Uni<ChatMessage> findById(String id);    
    Uni<ChatMessage> update(ChatMessage message); 

    Uni<List<ChatMessage>> findAllUnreadByChatGroup(String chatGroupId, String receiverId);
    Uni<List<ChatMessage>> updateAll(List<ChatMessage> messages);

    
    Uni<String> getLastMessage(String chatGroupId);
    Uni<Instant> getLastMessageDate(String chatGroupId);
}
