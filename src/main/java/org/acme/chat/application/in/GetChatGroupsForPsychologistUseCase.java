package org.acme.chat.application.in;

import java.util.List;

import org.acme.shared.ChatSidebar.ChatSidebarDTO;

import io.smallrye.mutiny.Uni;

public interface GetChatGroupsForPsychologistUseCase {
     Uni<List<ChatSidebarDTO>> getChatGroupsByPsychologistId(String psychologistId);
 
}
