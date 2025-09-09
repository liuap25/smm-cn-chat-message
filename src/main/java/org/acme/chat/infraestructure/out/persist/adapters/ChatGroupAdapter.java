package org.acme.chat.infraestructure.out.persist.adapters;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.acme.chat.application.out.ChatGroupRepositoryPort;
import org.acme.chat.domain.entity.ChatGroupEntity;
import org.acme.chat.domain.entity.ChatMessageEntity;
import org.acme.chat.domain.model.ChatGroup;
import org.acme.chat.infraestructure.mapper.ChatGroupMapper;
import org.acme.chat.infraestructure.out.persist.repository.ChatGroupRepository;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ChatGroupAdapter implements ChatGroupRepositoryPort {

    @Inject
    ChatGroupRepository chatGroupRepository;

    @Inject
    ChatGroupMapper mapper;

    @Override
     public Uni<ChatGroup> save(ChatGroup chatGroup) {
        ChatGroupEntity entity = mapper.toEntity(chatGroup);
        return chatGroupRepository.createChatGroup(entity)
                         .map(mapper::toDomain);
    }

    @Override
    @WithSession
    public Uni<ChatGroup> findById(String chatGroupId) {
        return chatGroupRepository.findById(UUID.fromString(chatGroupId))
                                .map(mapper::toDomain);
    }


    @Override
    public Uni<List<ChatGroup>> findChatGroupsByUserId(String userId) {
        return chatGroupRepository.findChatGroupsByUserId(userId)
                .map(entities -> entities.stream()
                        .map(mapper::toDomain)
                        .collect(Collectors.toList()));
    }

    @Override
    public Uni<ChatMessageEntity> findLastMessage(String chatGroupId) {
        return chatGroupRepository.findLastMessage(chatGroupId);
    }

    @Override
    public Uni<Long> countUnreadMessages(String chatGroupId, String userId) {
        return chatGroupRepository.countUnreadMessages(UUID.fromString(chatGroupId), userId);
    }

    @Override
    public Uni<List<ChatGroup>> findUnreadChatGroupsByUserId(String userId) {
        return chatGroupRepository.findUnreadChatGroupsByUserId(userId)
                .map(entities -> entities.stream()
                        .map(mapper::toDomain)
                        .collect(Collectors.toList()));
    }



    



    
    }




    


    

