package org.acme.chat.infraestructure.out.persist.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.acme.chat.domain.entity.ChatMessageEntity;
import org.hibernate.reactive.mutiny.Mutiny;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ChatMessageRepository implements PanacheRepositoryBase<ChatMessageEntity, UUID> {

    @Inject
    Mutiny.SessionFactory sessionFactory;

    public Uni<ChatMessageEntity> createMessage(ChatMessageEntity entity) {
        return sessionFactory.withTransaction(session ->
            session.persist(entity)
                   .call(session::flush)
                   .replaceWith(entity)
        );
    }


     public Uni<ChatMessageEntity> findById(UUID messageId) {
        return sessionFactory.withSession(session ->
            session.find(ChatMessageEntity.class, messageId)
        );
    }

    // Actualizar mensaje
    public Uni<ChatMessageEntity> update(ChatMessageEntity entity) {
        return sessionFactory.withTransaction(session ->
            session.merge(entity)
                   .call(session::flush)
        );
    }


    public Uni<List<ChatMessageEntity>> findAllUnreadByChatGroupId(UUID chatGroupId, String receiverId) {
        String query = "FROM ChatMessageEntity m WHERE m.chatGroup.id = :chatGroupId AND m.receiverId = :receiverId AND m.readAt IS NULL ORDER BY m.sentAt ASC";
        return sessionFactory.withSession(session ->
            session.createQuery(query, ChatMessageEntity.class)
                   .setParameter("chatGroupId", chatGroupId)
                   .setParameter("receiverId", receiverId)
                   .getResultList()
        );
    }

    // Actualizar todos los mensajes (merge) en la base de datos
   public Uni<List<ChatMessageEntity>> updateAll(List<ChatMessageEntity> messages) {
    // Convertimos cada mensaje en un Uni de actualización
    List<Uni<ChatMessageEntity>> updates = messages.stream()
            .map(msg -> sessionFactory.withTransaction(session -> session.merge(msg).call(session::flush)))
            .toList();

    // Combinamos todos los Uni en un Uni<List<ChatMessageEntity>>
    return Multi.createFrom().iterable(updates)
                .onItem().transformToUniAndMerge(u -> u)
                .collect().asList();
    }



    public Multi<ChatMessageEntity> findByChatGroupId(UUID chatGroupId) {
        return sessionFactory.withSession(session ->
            session.createQuery("FROM ChatMessageEntity m WHERE m.chatGroup.id = :id", ChatMessageEntity.class)
                   .setParameter("id", chatGroupId)
                   .getResultList()
        )
        .onItem().transformToMulti(list -> Multi.createFrom().iterable(list));


    }

     public Uni<List<ChatMessageEntity>> findAllByChatGroupId(UUID chatGroupId,int offset,int limit) {
         return sessionFactory.withSession(session ->
        session.createQuery(
            "FROM ChatMessageEntity m WHERE m.chatGroup.id = :id ORDER BY m.sentAt DESC",
            ChatMessageEntity.class
        )
        .setParameter("id", chatGroupId)
        .setFirstResult(offset)   
        .setMaxResults(limit)    
        .getResultList()
    );
    }

     public Uni<String> getLastMessage(String chatGroupId) {
            return sessionFactory.withSession(session -> session
            .createQuery(
                "SELECT m.message FROM ChatMessageEntity m " +
                "WHERE m.chatGroup.id = :chatGroupId " +
                "ORDER BY m.sentAt DESC",
                String.class
            )
            .setParameter("chatGroupId", UUID.fromString(chatGroupId))  // ✅ Conversión necesaria
            .setMaxResults(1)
            .getSingleResultOrNull()
        );
         
    }
          public Uni<Instant> getLastMessageDate(String chatGroupId) {
            return sessionFactory.withSession(session ->
            session.createQuery(
                "SELECT m.sentAt FROM ChatMessageEntity m " +
                "WHERE m.chatGroup.id = :chatGroupId " +
                "ORDER BY m.sentAt DESC",
                Instant.class
            )
            .setParameter("chatGroupId", UUID.fromString(chatGroupId))
            .setMaxResults(1)
            .getSingleResultOrNull()
        );
    }



    
}
