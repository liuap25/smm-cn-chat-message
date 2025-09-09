package org.acme.chat.infraestructure.out.persist.adapters;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.acme.chat.application.out.ChatMessageRepositoryPort;
import org.acme.chat.domain.entity.ChatGroupEntity;
import org.acme.chat.domain.entity.ChatMessageEntity;
import org.acme.chat.domain.model.ChatMessage;
import org.acme.chat.infraestructure.mapper.ChatMessageMapper;
import org.acme.chat.infraestructure.out.persist.repository.ChatMessageRepository;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ChatMessageRepositoryAdapter implements ChatMessageRepositoryPort {

    @Inject
    ChatMessageRepository chatMessageRepository;

    @Inject
    ChatMessageMapper chatMessageMapper;

    @Override
    public Uni<ChatMessage> save(ChatMessage message) {
        ChatMessageEntity entity = new ChatMessageEntity();

        entity.setId(message.id() != null ? UUID.fromString(message.id()) : null);

        ChatGroupEntity chatGroup = new ChatGroupEntity();
        chatGroup.setId(UUID.fromString(message.chatGroupId()));
        entity.setChatGroup(chatGroup);

        entity.setSenderId(message.senderId());
        entity.setReceiverId(message.receiverId());
        entity.setMessage(message.message());
        entity.setSentAt(message.sentAt() != null ? message.sentAt() : Instant.now());
        entity.setReadAt(message.readAt());

        return chatMessageRepository.createMessage(entity)
                .map(chatMessageMapper::toDomain);
    }

    @Override
    public Multi<ChatMessage> findByChatGroupId(String chatGroupId) {
        return chatMessageRepository.findByChatGroupId(UUID.fromString(chatGroupId))
                                    .onItem().transform(chatMessageMapper::toDomain);
    }

    @Override
    public Uni<List<ChatMessage>> findAllByChatGroupId(String chatGroupId,int offset,int limit) {    
        return chatMessageRepository
        .findAllByChatGroupId(UUID.fromString(chatGroupId), offset, limit) // 👈 Pasamos offset y limit
        .onItem().transform(messages ->
            messages.stream()
                .map(chatMessageMapper::toDomain) // 👈 Convertimos cada entity a domain
                .toList()
        );
    }

    @Override
    public Uni<ChatMessage> findById(String id) {  
        UUID messageId = UUID.fromString(id);
        return chatMessageRepository.findById(messageId)
                .map(entity -> entity != null ? chatMessageMapper.toDomain(entity) : null);
    }

    @Override
    public Uni<ChatMessage> update(ChatMessage message) {
       
         ChatMessageEntity entity = new ChatMessageEntity();
         entity.setId(message.id() != null ? UUID.fromString(message.id()) : null);

        // Solo necesitamos actualizar campos existentes, en este caso readAt
         entity.setReadAt(message.readAt());

        return chatMessageRepository.update(entity)
            .map(chatMessageMapper::toDomain);
    }

    @Override
    public Uni<List<ChatMessage>> findAllUnreadByChatGroup(String chatGroupId, String receiverId) {
          UUID chatGroupUUID = UUID.fromString(chatGroupId);
        return chatMessageRepository.findAllUnreadByChatGroupId(chatGroupUUID, receiverId)
                .onItem().transform(list -> 
                    list.stream()
                        .map(chatMessageMapper::toDomain)
                        .collect(Collectors.toList())
                );
    }

    @Override
    public Uni<List<ChatMessage>> updateAll(List<ChatMessage> messages) {
         // Convertimos ChatMessage -> ChatMessageEntity usando el mapper
        List<ChatMessageEntity> entities = messages.stream()
            .map(chatMessageMapper::toEntity)
            .collect(Collectors.toList());

        // Actualizamos todos los mensajes en la base de datos
        return chatMessageRepository.updateAll(entities)
            .onItem().transform(entityList ->
                entityList.stream()
                        .map(chatMessageMapper::toDomain)
                        .collect(Collectors.toList())
            );
    }

    

    

    


    
}
