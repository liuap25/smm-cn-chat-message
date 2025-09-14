package org.acme.chat.application.in.message;

import org.acme.shared.ChatMessage.ChatMessageResponseDto;

import io.smallrye.mutiny.Uni;

public interface SendMessageByPatientUseCase {

    Uni<ChatMessageResponseDto> sendMessagePatient(String chatGroupId, String senderId, String ignoredReceiverId, String message);
    
}
