package org.acme.chat.infraestructure.out.persist.repository;

import java.util.List;
import java.util.UUID;

import org.acme.chat.domain.entity.ChatGroupEntity;
import org.acme.chat.domain.entity.ChatMessageEntity;
import org.hibernate.reactive.mutiny.Mutiny;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ChatGroupRepository implements PanacheRepositoryBase<ChatGroupEntity,UUID> {
    @Inject
    Mutiny.SessionFactory sessionFactory;

    // Guardar un chat group en la base de datos
    public Uni<ChatGroupEntity> createChatGroup(ChatGroupEntity entity) {
        return sessionFactory.withTransaction(session -> 
            session.persist(entity)
                   .call(session::flush)
                   .replaceWith(entity)
        );
    }

    public Uni<ChatGroupEntity> findByPsychologistIdAndPatientId(String psychologistId, String patientId) {
    return sessionFactory.withSession(session ->
        session.createQuery(
            "FROM ChatGroupEntity g WHERE g.psychologistId = :psyId AND g.patientId = :patId",
            ChatGroupEntity.class
        )
        .setParameter("psyId", psychologistId)
        .setParameter("patId", patientId)
        .getSingleResultOrNull()
    );
    }

    public Uni<ChatGroupEntity> findById(UUID chatGroupId) {
        return find("id", chatGroupId).firstResult();
    }


    public Uni<List<ChatGroupEntity>> findChatGroupsByUserId(String userId) {
        return sessionFactory.withSession(session ->
        session.createQuery(
            "FROM ChatGroupEntity c WHERE c.psychologistId = :userId OR c.patientId = :userId",
            ChatGroupEntity.class
        )
        .setParameter("userId", userId)
        .getResultList()
    );
    }

    // Buscar último mensaje de un chat
    public Uni<ChatMessageEntity> findLastMessage(String chatGroupId) {
    UUID uuid = UUID.fromString(chatGroupId);
    return sessionFactory.withSession(session ->
        session.createQuery(
            "FROM ChatMessageEntity m WHERE m.chatGroup.id = :chatGroupId ORDER BY m.sentAt DESC",
            ChatMessageEntity.class
        )
        .setParameter("chatGroupId", uuid)
        .setMaxResults(1)
        .getSingleResultOrNull()
    );
    }



     public Uni<List<ChatGroupEntity>> findUnreadChatGroupsByUserId(String userId) {
        return sessionFactory.withSession(session ->
            session.createQuery(
                "SELECT DISTINCT cg " +
                "FROM ChatGroupEntity cg " +
                "JOIN ChatMessageEntity cm ON cm.chatGroup.id = cg.id " +
                "WHERE cm.readAt IS NULL " +
                "AND cm.receiverId = :userId",
                ChatGroupEntity.class
            )
            .setParameter("userId", userId)
            .getResultList()
        );
    }

     public Uni<Long> countUnreadMessages(UUID chatGroupId, String userId) {
        return sessionFactory.withSession(session ->
            session.createQuery(
                "SELECT COUNT(cm) FROM ChatMessageEntity cm " +
                "WHERE cm.chatGroup.id = :chatGroupId " +
                "AND cm.receiverId = :userId " +
                "AND cm.readAt IS NULL",
                Long.class
            )
            .setParameter("chatGroupId", chatGroupId)
            .setParameter("userId", userId)
            .getSingleResult()
        );
    }

       public Uni<List<Object[]>> findChatSidebarRawData(String userId) {
        return sessionFactory.withSession(session ->
        session.createQuery("""
            SELECT 
                g.id,
                CASE 
                    WHEN g.patientId = :userId THEN g.psychologistId 
                    ELSE g.patientId 
                END AS otherUserId,
                lastMsg.sentAt,
                lastMsg.message,
                SUM(CASE WHEN m.readAt IS NULL AND m.receiverId = :userId THEN 1 ELSE 0 END)
            FROM ChatGroupEntity g
            LEFT JOIN ChatMessageEntity m ON m.chatGroup.id = g.id
            LEFT JOIN ChatMessageEntity lastMsg ON lastMsg.id = (
                SELECT m2.id
                FROM ChatMessageEntity m2
                WHERE m2.chatGroup.id = g.id
                ORDER BY m2.sentAt DESC
                LIMIT 1
            )
            WHERE g.patientId = :userId OR g.psychologistId = :userId
            GROUP BY g.id, g.patientId, g.psychologistId, lastMsg.sentAt, lastMsg.message
            ORDER BY lastMsg.sentAt DESC NULLS LAST
        """, Object[].class)
        .setParameter("userId", userId)
        .getResultList()
    );
    }
}


   





