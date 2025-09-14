package org.acme.chat.application.usecase.message;

import java.time.Instant;
import java.util.UUID;

import org.acme.chat.application.in.message.SendMessageUseCase;
import org.acme.chat.application.out.ChatGroupRepositoryPort;
import org.acme.chat.application.out.ChatMessageRepositoryPort;
import org.acme.chat.domain.exception.ChatMessageException;
import org.acme.chat.domain.model.ChatMessage;
import org.acme.chat.infraestructure.out.event.ChatMessagePublisher;
import org.acme.chat.infraestructure.out.persist.adapters.clients.PatientGraphQLClient;
import org.acme.chat.infraestructure.out.persist.adapters.clients.PsychologistGrapQLClient;
import org.acme.shared.PatientChatDto;
import org.acme.shared.PsychologistChatDto;
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

                if (!senderId.equals(chatGroup.psychologistId())) {
                    return Uni.createFrom().failure(new ChatMessageException("Solo el psicólogo puede usar este UseCase"));
                }

                String receiverId = chatGroup.patientId(); // siempre paciente

                ChatMessage chatMessage = new ChatMessage(
                    null,
                    chatGroupId,
                    senderId,
                    receiverId,
                    message,
                    Instant.now(),
                    null
                );

                return chatMessageRepository.save(chatMessage)
                    .onItem().transformToUni(savedMessage -> {

                        ChatMessageResponseDto dto = new ChatMessageResponseDto(
                            UUID.fromString(savedMessage.id()),
                            savedMessage.message(),
                            savedMessage.sentAt(),
                            UUID.fromString(chatGroupId),
                            savedMessage.readAt(),
                            savedMessage.senderId()
                        );

                        // Publicar mensaje universal
                        publisher.publish(dto);

                        return chatGroupRepository.countUnreadMessages(chatGroupId, receiverId)
                            .onItem().transformToUni(unreadCount -> {

                                // --- ALL del remitente (psicólogo) ---
                                Uni<ChatSidebarDTO> sidebarSender = patientClient.getChatPatient(receiverId)
                                    .onFailure().recoverWithItem(() -> new org.acme.shared.PatientChatDto(
                                        receiverId, "Paciente desconocido", "default-patient.jpg"))
                                    .onItem().transform(patient -> {
                                        ChatSidebarDTO sidebarDto = new ChatSidebarDTO(
                                            chatGroupId,
                                            receiverId,
                                            patient.getFullname(),
                                            patient.getPhotoUrl(),
                                            savedMessage.message(),
                                            savedMessage.sentAt(),
                                            0 // remitente siempre 0
                                        );
                                        publisher.publishPsychologistSidebar(sidebarDto); // ALL
                                        return sidebarDto;
                                    });

                                // --- ALL y UNREAD del receptor (paciente) ---
                                Uni<ChatSidebarDTO> sidebarReceiver = psychologistClient.getPsychologistByUserId(senderId)
                                    .onFailure().recoverWithItem(() -> new org.acme.shared.PsychologistChatDto(
                                        senderId, "Psicólogo desconocido", "default-psychologist.jpg"))
                                    .onItem().transform(psych -> {
                                        ChatSidebarDTO sidebarDto = new ChatSidebarDTO(
                                            chatGroupId,
                                            senderId,
                                            psych.getFullName(),
                                            psych.getPhotoUrl(),
                                            savedMessage.message(),
                                            savedMessage.sentAt(),
                                            unreadCount.intValue() // real
                                        );
                                        publisher.publishPatientSidebar(sidebarDto); // ALL
                                        if (unreadCount > 0) {
                                            publisher.publishPatientSidebarUnread(sidebarDto); // UNREAD
                                        }
                                        return sidebarDto;
                                    });

                                return Uni.combine().all().unis(sidebarSender, sidebarReceiver)
                                        .discardItems()
                                        .onItem().transform(ignore -> dto);
                            });
                    });
            });
    }
    
}
