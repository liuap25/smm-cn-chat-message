package org.acme.chat.application.usecase.message;

import org.acme.chat.application.in.GetMessageByIdUseCase;
import org.acme.chat.application.out.ChatMessageRepositoryPort;
import org.acme.chat.domain.exception.ChatMessageException;
import org.acme.chat.domain.model.ChatMessage;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetMessageByIdUseCaseImpl implements GetMessageByIdUseCase {

    @Inject
    ChatMessageRepositoryPort chatMessageRepository;

    @Override
    public Uni<ChatMessage> getMessageById(String messageId) {
         return chatMessageRepository.findById(messageId)
                .onItem().ifNull().failWith(() -> new ChatMessageException("Mensaje no encontrado"));
    }

    




    
}
