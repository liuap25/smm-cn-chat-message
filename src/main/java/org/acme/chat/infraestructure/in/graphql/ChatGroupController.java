package org.acme.chat.infraestructure.in.graphql;

import org.acme.chat.application.in.CreateChatGroupUseCase;
import org.acme.chat.application.out.ChatGroupRepositoryPort;
import org.acme.chat.domain.exception.ChatGroupCreationException;
import org.acme.chat.domain.model.ChatGroup;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.*;

@GraphQLApi
@ApplicationScoped
public class ChatGroupController {

    @Inject
    CreateChatGroupUseCase createChatGroupUseCase;

    @Inject
    ChatGroupRepositoryPort chatGroupRepository;

    // Query opcional para obtener un chat group por psicólogo y paciente
    @Query("getChatGroup")
    public Uni<ChatGroup> getChatGroup(
            @Name("psychologistId") String psychologistId,
            @Name("patientId") String patientId
    ) {
        // Aquí podrías agregar un use case para obtenerlo, si lo implementas
        return Uni.createFrom().failure(new UnsupportedOperationException("No implementado"));
    }

    // Mutation para crear un chat group
    @Mutation("createChatGroup")
    public Uni<ChatGroup> createChatGroup(
            @Name("psychologistId") String psychologistId,
            @Name("patientId") String patientId
    ) {
        return createChatGroupUseCase.createChatGroup(psychologistId, patientId)
                .onFailure(ChatGroupCreationException.class)
                .transform(failure -> new GraphQLException(failure.getMessage()));
    }

     @Query("getChatGroupById")
     public Uni<ChatGroup> getChatGroupById(@Name("chatGroupId") String chatGroupId) {
        return chatGroupRepository.findById(chatGroupId);
    }
    
}
