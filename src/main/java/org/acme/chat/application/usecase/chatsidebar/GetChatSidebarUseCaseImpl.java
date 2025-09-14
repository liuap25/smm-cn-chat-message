package org.acme.chat.application.usecase.chatsidebar;

import java.util.List;

import org.acme.chat.application.in.chatsidebar.GetChatSidebarUseCase;
import org.acme.chat.application.out.ChatSidebarRepositoryPort;
import org.acme.shared.ChatSidebar.ChatSidebarRawDTO;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetChatSidebarUseCaseImpl implements GetChatSidebarUseCase {

    @Inject
    ChatSidebarRepositoryPort chatSidebarRepository;

    @Override
    public Uni<List<ChatSidebarRawDTO>> getChatSidebar(String userId) {
        return chatSidebarRepository.findChatSidebarDataExample(userId);
    }


}
