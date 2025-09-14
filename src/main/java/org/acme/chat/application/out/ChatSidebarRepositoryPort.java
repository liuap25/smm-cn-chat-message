package org.acme.chat.application.out;

import java.util.List;

import org.acme.shared.ChatSidebar.ChatSidebarDTO;
import org.acme.shared.ChatSidebar.ChatSidebarRawDTO;

import io.smallrye.mutiny.Uni;

public interface ChatSidebarRepositoryPort {
    Uni<List<ChatSidebarRawDTO>> findChatSidebarDataExample(String userId);
    Uni<List<ChatSidebarDTO>> findChatSidebarData(String userId);
    
}
