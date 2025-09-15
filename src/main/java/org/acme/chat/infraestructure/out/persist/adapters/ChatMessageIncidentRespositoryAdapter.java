package org.acme.chat.infraestructure.out.persist.adapters;



import org.acme.chat.application.out.ChatMessageIncidentRepositoryPort;
import org.acme.chat.domain.entity.ChatMessageIncidentEntity;
import org.acme.chat.domain.model.ChatMessageIncident;
import org.acme.chat.infraestructure.mapper.ChatMessageIncidentMapper;
import org.acme.chat.infraestructure.out.persist.repository.ChatMessageIncidentRepository;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ChatMessageIncidentRespositoryAdapter implements ChatMessageIncidentRepositoryPort{


    @Inject
    ChatMessageIncidentRepository chatMessageIncidentRepository;

    @Inject
    ChatMessageIncidentMapper mapper;

    @Override
    public Uni<ChatMessageIncident> save(ChatMessageIncident incident) {
        
        ChatMessageIncidentEntity entity = mapper.toEntity(incident);
        return chatMessageIncidentRepository.save(entity)
                            .map(mapper::toDomain);

    }



    
    
    
}
