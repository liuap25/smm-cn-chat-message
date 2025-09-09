package org.acme.chat.application.in;

import org.acme.chat.domain.model.ChatMessage;

import io.smallrye.mutiny.Uni;

public interface GetMessageByIdUseCase {
    
    Uni<ChatMessage> getMessageById(String messageId);
}
