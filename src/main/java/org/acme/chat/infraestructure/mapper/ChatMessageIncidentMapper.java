package org.acme.chat.infraestructure.mapper;

import org.acme.chat.domain.entity.ChatMessageIncidentEntity;
import org.acme.chat.domain.model.ChatMessageIncident;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface ChatMessageIncidentMapper {

     @Mapping(source = "chatMessage.id", target = "chatMessageId")
    ChatMessageIncident toDomain(ChatMessageIncidentEntity entity);

    @Mapping(target = "chatMessage.id", source = "chatMessageId")
    ChatMessageIncidentEntity toEntity(ChatMessageIncident incident);

    
}
