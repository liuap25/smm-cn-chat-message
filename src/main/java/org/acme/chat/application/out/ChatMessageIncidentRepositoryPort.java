package org.acme.chat.application.out;

import org.acme.chat.domain.model.ChatMessageIncident;

import io.smallrye.mutiny.Uni;

public interface ChatMessageIncidentRepositoryPort {

    Uni<ChatMessageIncident>save(ChatMessageIncident incident);
    
}
