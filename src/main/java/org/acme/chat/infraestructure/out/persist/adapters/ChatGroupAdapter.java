package org.acme.chat.infraestructure.out.persist.adapters;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.acme.chat.application.out.ChatGroupRepositoryPort;
import org.acme.chat.domain.entity.ChatGroupEntity;
import org.acme.chat.domain.entity.ChatMessageEntity;
import org.acme.chat.domain.model.ChatGroup;
import org.acme.chat.infraestructure.mapper.ChatGroupMapper;
import org.acme.chat.infraestructure.out.persist.adapters.clients.PatientGraphQLClient;
import org.acme.chat.infraestructure.out.persist.adapters.clients.PsychologistGrapQLClient;
import org.acme.chat.infraestructure.out.persist.repository.ChatGroupRepository;
import org.acme.chat.infraestructure.out.persist.repository.ChatMessageRepository;
import org.acme.shared.ChatSidebar.ChatSidebarDTO;

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

    @Inject
    PsychologistGrapQLClient  psychologistClient;

    @Inject
    ChatMessageRepository chatMessageRepository;

   

    @Inject
    PatientGraphQLClient patientClient;


    @Override
     public Uni<ChatGroup> save(ChatGroup chatGroup) {
        ChatGroupEntity entity = mapper.toEntity(chatGroup);
        return chatGroupRepository.createChatGroup(entity)
                         .map(mapper::toDomain);

    }


    @Override
    public Uni<ChatGroup> findByPsychologistIdAndPatientId(String psychologistId, String patientId) {
        return chatGroupRepository.findByPsychologistIdAndPatientId(psychologistId, patientId)
            .onItem().ifNotNull().transform(mapper::toDomain);
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

    @Override
    public Uni<ChatSidebarDTO> getSidebarDTO(String chatGroupId, String readerId) {
            
         return findById(chatGroupId)
        .onItem().ifNull().continueWith(() -> null)
        .onItem().transformToUni(chatGroup -> {
            if (chatGroup == null) {
                return Uni.createFrom().nullItem();
            }

            // Determinar el otro usuario
            String otherUserId = readerId.equals(chatGroup.psychologistId())
                    ? chatGroup.patientId()
                    : chatGroup.psychologistId();

            // Uni para nombre y foto
            Uni<String> fullNameUni;
            Uni<String> photoUrlUni;

            if (readerId.equals(chatGroup.psychologistId())) {
                // El lector es psicólogo → el otro es paciente
                Uni<org.acme.shared.PatientChatDto> patientUni =
                        patientClient.getChatPatient(otherUserId)
                            .onFailure().recoverWithItem(() ->
                                    new org.acme.shared.PatientChatDto(
                                            otherUserId,
                                            "Paciente desconocido",
                                            "default-patient.jpg"
                                    )
                            );

                fullNameUni = patientUni.onItem().transform(p -> 
                        p.getFullname() != null ? p.getFullname() : "Paciente desconocido"
                );
                photoUrlUni = patientUni.onItem().transform(p -> 
                        p.getPhotoUrl() != null ? p.getPhotoUrl() : "default-patient.jpg"
                );

            } else {
                // El lector es paciente → el otro es psicólogo
                Uni<org.acme.shared.PsychologistChatDto> psyUni =
                        psychologistClient.getPsychologistByUserId(otherUserId)
                            .onFailure().recoverWithItem(() ->
                                    new org.acme.shared.PsychologistChatDto(
                                            otherUserId,
                                            "Psicólogo desconocido",
                                            "default-psychologist.jpg"
                                    )
                            );

                fullNameUni = psyUni.onItem().transform(p -> 
                        p.getFullName() != null ? p.getFullName() : "Psicólogo desconocido"
                );
                photoUrlUni = psyUni.onItem().transform(p -> 
                        p.getPhotoUrl() != null ? p.getPhotoUrl() : "default-psychologist.jpg"
                );
            }

            // Obtener último mensaje y fecha
            Uni<String> lastMessageUni = chatMessageRepository.getLastMessage(chatGroupId);
            Uni<Instant> lastMessageDateUni = chatMessageRepository.getLastMessageDate(chatGroupId);

            // Contar mensajes no leídos
            UUID chatGroupUUID = UUID.fromString(chatGroupId);
            Uni<Long> unreadCountUni = chatGroupRepository.countUnreadMessages(chatGroupUUID, readerId);

            // Combinar en el DTO final
            return Uni.combine().all().unis(
                    fullNameUni,
                    photoUrlUni,
                    lastMessageUni,
                    lastMessageDateUni,
                    unreadCountUni
            ).asTuple()
            .onItem().transform(tuple -> new ChatSidebarDTO(
                    chatGroupId,
                    otherUserId,
                    tuple.getItem1(), // fullName
                    tuple.getItem2(), // photoUrl
                    tuple.getItem3(), // lastMessage
                    tuple.getItem4(), // lastMessageDate
                    tuple.getItem5().intValue() // unreadCount
            ));
        });
    }


    @Override
    public Uni<ChatSidebarDTO> getSidebarUnreadDTO(String chatGroupId, String readerId) {    
        return findById(chatGroupId)
        .onItem().ifNull().continueWith(() -> null)
        .onItem().transformToUni(chatGroup -> {
            if (chatGroup == null) {
                return Uni.createFrom().nullItem();
            }

            // Determinar el otro usuario
            String otherUserId = readerId.equals(chatGroup.psychologistId())
                    ? chatGroup.patientId()
                    : chatGroup.psychologistId();

            // Uni para nombre y foto
            Uni<String> fullNameUni;
            Uni<String> photoUrlUni;

            if (readerId.equals(chatGroup.psychologistId())) {
                Uni<org.acme.shared.PatientChatDto> patientUni =
                        patientClient.getChatPatient(otherUserId)
                            .onFailure().recoverWithItem(() ->
                                    new org.acme.shared.PatientChatDto(
                                            otherUserId,
                                            "Paciente desconocido",
                                            "default-patient.jpg"
                                    )
                            );

                fullNameUni = patientUni.onItem().transform(p ->
                        p.getFullname() != null ? p.getFullname() : "Paciente desconocido"
                );
                photoUrlUni = patientUni.onItem().transform(p ->
                        p.getPhotoUrl() != null ? p.getPhotoUrl() : "default-patient.jpg"
                );

            } else {
                Uni<org.acme.shared.PsychologistChatDto> psyUni =
                        psychologistClient.getPsychologistByUserId(otherUserId)
                            .onFailure().recoverWithItem(() ->
                                    new org.acme.shared.PsychologistChatDto(
                                            otherUserId,
                                            "Psicólogo desconocido",
                                            "default-psychologist.jpg"
                                    )
                            );

                fullNameUni = psyUni.onItem().transform(p ->
                        p.getFullName() != null ? p.getFullName() : "Psicólogo desconocido"
                );
                photoUrlUni = psyUni.onItem().transform(p ->
                        p.getPhotoUrl() != null ? p.getPhotoUrl() : "default-psychologist.jpg"
                );
            }

            // Último mensaje y fecha
            Uni<String> lastMessageUni = chatMessageRepository.getLastMessage(chatGroupId);
            Uni<Instant> lastMessageDateUni = chatMessageRepository.getLastMessageDate(chatGroupId);

            // Contar solo mensajes NO LEÍDOS
            UUID chatGroupUUID = UUID.fromString(chatGroupId);
            Uni<Long> unreadCountUni = chatGroupRepository.countUnreadMessages(chatGroupUUID, readerId);

            // Combinar en DTO solo si unreadCount > 0
            return unreadCountUni.onItem().transformToUni(unreadCount -> {
                if (unreadCount == 0) {
                    return Uni.createFrom().nullItem(); // No se publica en UNREAD
                }

                return Uni.combine().all().unis(
                        fullNameUni,
                        photoUrlUni,
                        lastMessageUni,
                        lastMessageDateUni
                ).asTuple()
                .onItem().transform(tuple -> new ChatSidebarDTO(
                        chatGroupId,
                        otherUserId,
                        tuple.getItem1(), // fullName
                        tuple.getItem2(), // photoUrl
                        tuple.getItem3(), // lastMessage
                        tuple.getItem4(), // lastMessageDate
                        unreadCount.intValue() // unreadCount
                ));
            });
        });
    }




    
}


    



    



    
    




    


    

