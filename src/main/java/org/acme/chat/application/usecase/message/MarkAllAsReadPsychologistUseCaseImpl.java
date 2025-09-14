package org.acme.chat.application.usecase.message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.acme.chat.application.in.message.MarkAllAsReadPsychologistUseCase;
import org.acme.chat.application.out.ChatGroupRepositoryPort;
import org.acme.chat.application.out.ChatMessageRepositoryPort;
import org.acme.chat.domain.exception.ChatMessageException;
import org.acme.chat.domain.model.ChatMessage;
import org.acme.chat.infraestructure.out.event.ChatMessagePublisher;
import org.acme.shared.ChatMessage.ChatMessageResponseDto;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MarkAllAsReadPsychologistUseCaseImpl implements MarkAllAsReadPsychologistUseCase {

    @Inject
    ChatMessageRepositoryPort chatMessageRepository;

    @Inject
    ChatMessagePublisher publisher;

    @Inject
    ChatGroupRepositoryPort chatGroupRepository;



    @Override
    public Uni<Boolean> markAllAsReadPsychologist(String chatGroupId, String readerId) {
        
         Instant now = Instant.now();

    return chatMessageRepository.findAllUnreadByChatGroup(chatGroupId, readerId)
        .onItem().ifNull().failWith(() -> new ChatMessageException("No hay mensajes pendientes"))
        .onItem().transformToUni(messages -> {
            // Marcar todos los mensajes como leídos
            List<ChatMessage> updatedMessages = messages.stream()
                .map(msg -> new ChatMessage(
                    msg.id(),
                    msg.chatGroupId(),
                    msg.senderId(),
                    msg.receiverId(),
                    msg.message(),
                    msg.sentAt(),
                    now
                ))
                .collect(Collectors.toList());

            // Actualizar en la DB
            return chatMessageRepository.updateAll(updatedMessages)
                .onItem().transformToUni(updated -> {

                    // Publicar mensajes individuales actualizados
                    updated.forEach(msg -> publisher.publish(
                        new ChatMessageResponseDto(
                            UUID.fromString(msg.id()),
                            msg.message(),
                            msg.sentAt(),
                            UUID.fromString(msg.chatGroupId()),
                            msg.readAt() != null ? msg.readAt() : now,
                            msg.senderId()
                        )
                    ));

                    // Publicar sidebar ALL actualizado
                    return chatGroupRepository.getSidebarDTO(chatGroupId, readerId)
                        .onItem().transformToUni(sidebar -> {
                            if (sidebar != null) {
                                System.out.println("📤 Publicando actualización de sidebar (Psychologist): " + sidebar);
                                publisher.publishPsychologistSidebar(sidebar); // ALL
                            }

                            // Publicar sidebar UNREAD actualizado
                            return chatGroupRepository.getSidebarUnreadDTO(chatGroupId, readerId)
                                .onItem().transformToUni(unreadSidebar -> {
                                    if (unreadSidebar != null) {
                                        System.out.println("📩 Sidebar UNREAD Psychologist actualizado: " + unreadSidebar);
                                        publisher.publishPsychologistSidebarUnread(unreadSidebar); // UNREAD
                                    } else {
                                        System.out.println("📩 Sidebar UNREAD Psychologist: todos los mensajes están leídos (unreadCount=0)");
                                    }
                                    return Uni.createFrom().item(true);
                                });
                        });
                });
        });
    }

}
    

