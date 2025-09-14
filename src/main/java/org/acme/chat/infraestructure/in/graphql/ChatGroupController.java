package org.acme.chat.infraestructure.in.graphql;

import org.acme.chat.application.in.chatgroup.CreateChatGroupUseCase;
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
    

    @Mutation("createChatGroup")
    public Uni<ChatGroup> createChatGroup(@Name("psychologistId") String psychologistId,@Name("patientId") String patientId){
        return createChatGroupUseCase.createChatGroup(psychologistId, patientId)
                .onFailure(ChatGroupCreationException.class)
                .transform(failure -> new GraphQLException(failure.getMessage()));
    }

     @Query("getChatGroupById")
     public Uni<ChatGroup> getChatGroupById(@Name("chatGroupId") String chatGroupId) {
        return chatGroupRepository.findById(chatGroupId);
    }
    
}
