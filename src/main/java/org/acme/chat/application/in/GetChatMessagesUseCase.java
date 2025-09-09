package org.acme.chat.application.in;



import org.acme.shared.GetMessages.ChatConversationDTO;


import io.smallrye.mutiny.Uni;

public interface GetChatMessagesUseCase {
     Uni<ChatConversationDTO> getMessagesByChatGroup(String chatGroupId, String currentUserId,int offset,int limit);
}
