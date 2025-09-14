package org.acme.chat.infraestructure.in.graphql;


import java.util.UUID;

import org.acme.chat.application.in.message.GetChatMessagesUseCase;
import org.acme.chat.application.in.message.GetMessageByIdUseCase;
import org.acme.chat.application.in.message.MarkAllAsReadPsychologistUseCase;
import org.acme.chat.application.in.message.MarkAllMessagesAsReadUseCase;
import org.acme.chat.application.in.message.SendMessageByPatientUseCase;
import org.acme.chat.application.in.message.SendMessageUseCase;
import org.acme.chat.domain.model.ChatMessage;
import org.acme.chat.infraestructure.out.event.ChatMessagePublisher;
import org.acme.chat.infraestructure.out.event.ChatMessageSubscriber;
import org.acme.shared.ChatMessage.ChatMessageResponseDto;
import org.acme.shared.GetMessages.ChatConversationDTO;


import io.smallrye.graphql.api.Subscription;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.*;

@GraphQLApi
@ApplicationScoped
public class ChatMessageController {
    @Inject
    SendMessageUseCase sendMessageUseCase;

    @Inject
    SendMessageByPatientUseCase sendMessageByPatientUseCase;

    @Inject
    GetChatMessagesUseCase getChatMessagesUseCase;

    @Inject
    ChatMessagePublisher publisher;

    @Inject
    ChatMessageSubscriber subscriber;

    @Inject
    GetMessageByIdUseCase getMessageByIdUseCase;

    @Inject
    MarkAllMessagesAsReadUseCase markMessageAsReadUseCase;

    @Inject
    MarkAllMessagesAsReadUseCase markAllMessagesAsReadUseCase;

    @Inject
    MarkAllAsReadPsychologistUseCase markAllAsReadPsychologistUseCase;



    @Mutation
    public Uni<ChatMessageResponseDto> sendMessage(
            @Name("chatGroupId") String chatGroupId,
            @Name("senderId") String senderId,
            @Name("receiverId") String receiverId,
            @Name("message") String message
    ) {
        return sendMessageUseCase.sendMessage(chatGroupId, senderId, receiverId, message);
    }


    @Mutation
    public Uni<ChatMessageResponseDto> sendMessagePatient(
            @Name("chatGroupId") String chatGroupId,
            @Name("senderId") String senderId,
            @Name("receiverId") String receiverId,
            @Name("message") String message
    ) {
        return sendMessageByPatientUseCase.sendMessagePatient(chatGroupId, senderId, receiverId, message);
    }







    @Subscription
    public Multi<ChatMessageResponseDto> subscribeMessages(@Name("chatGroupId") String chatGroupId) {
      UUID groupId = UUID.fromString(chatGroupId);
      return subscriber.subscribe()
                      .filter(msg -> msg.getChatGroupId().equals(groupId));
      }


    @Query("GetChatMessagesByGroup")
    @Description("Obtiene mensajes de un chat group específico con paginación")
    public Uni<ChatConversationDTO> getChatMessagesByGroup(String chatGroupId, String currentUserId,int offset,int limit) {
        return getChatMessagesUseCase.getMessagesByChatGroup(chatGroupId, currentUserId,offset, limit);
    }

    @Query("GetMessageById")
    public Uni<ChatMessage> getMessageById(String messageId) {
        return getMessageByIdUseCase.getMessageById(messageId);
    }

    @Mutation("markAllMessagesAsRead")
    public Uni<Boolean> markAllMessagesAsRead(
            @Name("chatGroupId") String chatGroupId,
            @Name("readerId") String readerId
    ) {
        return markAllMessagesAsReadUseCase.markAllAsRead(chatGroupId, readerId);
    }

    @Mutation("MarkAllAsReadPsychologis")
    public Uni<Boolean> markAllAsReadPsychologist(
            @Name("chatGroupId") String chatGroupId,
            @Name("readerId") String readerId
    ) {
        return markAllAsReadPsychologistUseCase.markAllAsReadPsychologist(chatGroupId, readerId);
    }
    


}
