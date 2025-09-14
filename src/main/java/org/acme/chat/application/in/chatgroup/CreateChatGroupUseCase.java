package org.acme.chat.application.in.chatgroup;

import org.acme.chat.domain.model.ChatGroup;

import io.smallrye.mutiny.Uni;

public interface CreateChatGroupUseCase {
    Uni<ChatGroup> createChatGroup(String psychologistId, String patientId);
    
}
