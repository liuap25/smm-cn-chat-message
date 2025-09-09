package org.acme.chat.application.usecase;

import java.time.Instant;
import java.util.UUID;

import org.acme.chat.application.in.SendMessageUseCase;
import org.acme.chat.application.out.ChatGroupRepositoryPort;
import org.acme.chat.application.out.ChatMessageRepositoryPort;
import org.acme.chat.domain.exception.ChatMessageException;
import org.acme.chat.domain.model.ChatMessage;
import org.acme.chat.infraestructure.out.event.ChatMessagePublisher;
import org.acme.chat.infraestructure.out.persist.adapters.clients.PatientGraphQLClient;
import org.acme.chat.infraestructure.out.persist.adapters.clients.PsychologistGrapQLClient;
import org.acme.shared.ChatMessage.ChatMessageResponseDto;
import org.acme.shared.ChatSidebar.ChatSidebarDTO;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SendMessageUseCaseImpl implements SendMessageUseCase {

    @Inject
    ChatMessageRepositoryPort chatMessageRepository;

    @Inject
    ChatGroupRepositoryPort chatGroupRepository;

    @Inject
    ChatMessagePublisher publisher;

    @Inject
    PatientGraphQLClient patientClient;

    @Inject
    PsychologistGrapQLClient psychologistClient;

    @Override
    @WithTransaction
    public Uni<ChatMessageResponseDto> sendMessage(String chatGroupId, String senderId, String ignoredReceiverId, String message) {

         return chatGroupRepository.findById(chatGroupId)
            .onItem().ifNull().failWith(() -> new ChatMessageException("ChatGroup no existe"))
            .onItem().transformToUni(chatGroup -> {

                // Validar que el sender pertenece al grupo
                if (!senderId.equals(chatGroup.psychologistId()) &&
                    !senderId.equals(chatGroup.patientId())) {
                    return Uni.createFrom().failure(
                            new ChatMessageException("El remitente no pertenece al chat")
                    );
                }

                // Determinar receiver
                String receiverId = senderId.equals(chatGroup.patientId())
                        ? chatGroup.psychologistId()
                        : chatGroup.patientId();

                // Crear entidad ChatMessage
                ChatMessage chatMessage = new ChatMessage(
                        null,
                        chatGroupId,
                        senderId,
                        receiverId,
                        message,
                        Instant.now(),
                        null
                );

                // Guardar mensaje en DB
                return chatMessageRepository.save(chatMessage)
                        .onItem().transform(savedMessage -> {
                            // Crear DTO sin obtener datos del sender
                            ChatMessageResponseDto dto = new ChatMessageResponseDto(
                                    UUID.fromString(savedMessage.id()),
                                    savedMessage.message(),
                                    savedMessage.sentAt(),
                                    UUID.fromString(chatGroupId),
                                    savedMessage.readAt()
                            );

                            // Publicar DTO
                            publisher.publish(dto);


                             // 🚀 NUEVO: Crear DTO para la sidebar
                        ChatSidebarDTO sidebarDto = new ChatSidebarDTO(
                                chatGroupId,   
                                receiverId,                  
                                null,                         
                                null,                         
                                savedMessage.message(),       
                                savedMessage.sentAt(),        
                                0                             
                        );

                        
                        publisher.publishSidebarUpdate(sidebarDto);

                        return dto;
                        });
            });
    }
    
}
