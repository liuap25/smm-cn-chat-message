package org.acme.chat.infraestructure.mapper;

import org.acme.chat.domain.entity.ChatGroupEntity;
import org.acme.chat.domain.model.ChatGroup;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "cdi")
public interface ChatGroupMapper {
    ChatGroupMapper INSTANCE = Mappers.getMapper(ChatGroupMapper.class);

    ChatGroupEntity toEntity(ChatGroup chatGroup);
    ChatGroup toDomain(ChatGroupEntity entity);
    
}
