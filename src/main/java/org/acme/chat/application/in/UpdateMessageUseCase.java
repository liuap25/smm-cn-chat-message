package org.acme.chat.application.in;

import org.acme.chat.domain.model.ChatMessage;

import io.smallrye.mutiny.Uni;

public interface UpdateMessageUseCase {
     Uni<ChatMessage> updateMessage(ChatMessage message);  
}
