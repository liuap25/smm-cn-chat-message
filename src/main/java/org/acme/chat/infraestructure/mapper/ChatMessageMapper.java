package org.acme.chat.infraestructure.mapper;

import org.acme.chat.domain.entity.ChatMessageEntity;
import org.acme.chat.domain.model.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface ChatMessageMapper {
    @Mapping(source = "chatGroup.id", target = "chatGroupId")
    ChatMessage toDomain(ChatMessageEntity entity);

    @Mapping(target = "chatGroup.id", source = "chatGroupId")
    ChatMessageEntity toEntity(ChatMessage message);
    
}
