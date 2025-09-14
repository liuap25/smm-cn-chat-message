package org.acme.chat.application.in.chatsidebar;

import java.util.List;

import org.acme.shared.ChatSidebar.ChatSidebarRawDTO;

import io.smallrye.mutiny.Uni;

public interface GetChatSidebarUseCase {
     Uni<List<ChatSidebarRawDTO>> getChatSidebar(String userId);
    
}
