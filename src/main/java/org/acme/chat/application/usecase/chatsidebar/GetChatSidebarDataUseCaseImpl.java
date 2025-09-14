package org.acme.chat.application.usecase.chatsidebar;

import java.util.List;

import org.acme.chat.application.in.chatsidebar.GetChatSidebarDataUseCase;
import org.acme.chat.infraestructure.out.persist.adapters.ChatSidebarRepositoryAdapter;
import org.acme.shared.ChatSidebar.ChatSidebarDTO;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetChatSidebarDataUseCaseImpl implements GetChatSidebarDataUseCase{

    @Inject
    ChatSidebarRepositoryAdapter chatSidebarAdapter;

    @Override
    public Uni<List<ChatSidebarDTO>> findChatSidebarData(String userId) {
        return chatSidebarAdapter.findChatSidebarData(userId);
    }

    
}
