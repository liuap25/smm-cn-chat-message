package org.acme.chat.infraestructure.out.persist.repository;

import java.util.UUID;

import org.acme.chat.domain.entity.ChatMessageIncidentEntity;
import org.hibernate.reactive.mutiny.Mutiny;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ChatMessageIncidentRepository implements PanacheRepositoryBase<ChatMessageIncidentEntity,UUID>{

    @Inject
    Mutiny.SessionFactory sessionFactory;


    public Uni<ChatMessageIncidentEntity> save(ChatMessageIncidentEntity entity) {
         return sessionFactory.withTransaction(session -> 
            session.persist(entity)
                   .call(session::flush)
                   .replaceWith(entity)
        );
    }  
    
    

}
