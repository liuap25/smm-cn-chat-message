package org.acme.chat.application.usecase.message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.acme.chat.application.in.message.MarkAllMessagesAsReadUseCase;
import org.acme.chat.application.out.ChatMessageRepositoryPort;
import org.acme.chat.domain.exception.ChatMessageException;
import org.acme.chat.domain.model.ChatMessage;
import org.acme.chat.infraestructure.out.event.ChatMessagePublisher;
import org.acme.shared.ChatMessage.ChatMessageResponseDto;
import org.acme.shared.ChatSidebar.ChatSidebarDTO;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@ApplicationScoped
public class MarkMessageAsReadUseCaseImpl implements MarkAllMessagesAsReadUseCase{

     @Inject
    ChatMessageRepositoryPort chatMessageRepository;

    @Inject
    ChatMessagePublisher publisher;

   

    @Override
    public Uni<List<ChatMessageResponseDto>> markAllAsRead(String chatGroupId, String readerId) {
         return chatMessageRepository.findAllUnreadByChatGroup(chatGroupId, readerId)
                .onItem().ifNull().failWith(() -> new ChatMessageException("No hay mensajes pendientes"))
                .onItem().transformToUni(messages -> {
                    Instant now = Instant.now();

                    // Crear mensajes actualizados con readAt
                    List<ChatMessage> updatedMessages = messages.stream()
                            .map(msg -> new ChatMessage(
                                    msg.id(),
                                    msg.chatGroupId(),
                                    msg.senderId(),
                                    msg.receiverId(),
                                    msg.message(),
                                    msg.sentAt(),
                                    now // marcar como leído
                            ))
                            .collect(Collectors.toList());

                    // Actualizar todos los mensajes en la DB
                    return chatMessageRepository.updateAll(updatedMessages)
                            .onItem().transformToUni(updated -> {
                                // Crear DTOs
                                List<ChatMessageResponseDto> dtos = updated.stream()
                                        .map(msg -> new ChatMessageResponseDto(
                                                UUID.fromString(msg.id()),
                                                msg.message(),
                                                msg.sentAt(),
                                                UUID.fromString(msg.chatGroupId()),
                                                msg.readAt() != null ? msg.readAt() : now
                                        ))
                                        .collect(Collectors.toList());

                                // Publicar DTOs y sidebar dentro del flujo reactivo
                                return Uni.createFrom().item(dtos)
                                        .invoke(list -> {
                                            list.forEach(publisher::publish);
                                            publisher.publishSidebarUpdate(
                                                    new ChatSidebarDTO(
                                                            chatGroupId,
                                                            null,
                                                            null,
                                                            null,
                                                            null,
                                                            null,
                                                            0 // recalcular en el frontend
                                                    )
                                            );
                                        });
                            });
                });
    }
}

    





    

