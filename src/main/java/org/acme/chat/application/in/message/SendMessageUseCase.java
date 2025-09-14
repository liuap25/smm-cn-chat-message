package org.acme.chat.application.in.message;

import org.acme.shared.ChatMessage.ChatMessageResponseDto;

import io.smallrye.mutiny.Uni;

public interface SendMessageUseCase {
    Uni<ChatMessageResponseDto> sendMessage(String chatGroupId, String senderId, String ignoredReceiverId, String message);
    
}
