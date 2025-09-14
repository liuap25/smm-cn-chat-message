package org.acme.chat.application.usecase.chatsidebar;

import java.util.List;

import org.acme.chat.application.in.chatsidebarexample.GetPsychologistSidebarAllUseCase;
import org.acme.chat.infraestructure.out.persist.adapters.ChatSidebarRepositoryAdapter;
import org.acme.shared.ChatSidebar.ChatSidebarDTO;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetPsychologistSidebarAllUseCaseImpl implements GetPsychologistSidebarAllUseCase{
    
    @Inject
    ChatSidebarRepositoryAdapter repository;

    @Override
    public Uni<List<ChatSidebarDTO>> execute(String psychologistUserId) {
        return repository.findChatSidebarData(psychologistUserId);
    }


    
}
