package org.acme.chat.application.usecase.message;

import org.acme.chat.application.in.message.UpdateMessageUseCase;
import org.acme.chat.application.out.ChatMessageRepositoryPort;
import org.acme.chat.domain.model.ChatMessage;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UpdateMessageUseCaseImpl implements UpdateMessageUseCase{

    @Inject
    ChatMessageRepositoryPort chatMessageRepository;

    @Override
    public Uni<ChatMessage> updateMessage(ChatMessage message) {        
         return chatMessageRepository.update(message);
    }

    
    
}
