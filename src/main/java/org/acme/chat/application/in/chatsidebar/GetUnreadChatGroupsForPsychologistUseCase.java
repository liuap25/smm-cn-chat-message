package org.acme.chat.application.in.chatsidebar;

import java.util.List;

import org.acme.shared.ChatSidebar.ChatSidebarDTO;

import io.smallrye.mutiny.Uni;

public interface GetUnreadChatGroupsForPsychologistUseCase {
    Uni<List<ChatSidebarDTO>> getUnreadChatGroupsForPsychologist(String psychologistUserId);
    
} 