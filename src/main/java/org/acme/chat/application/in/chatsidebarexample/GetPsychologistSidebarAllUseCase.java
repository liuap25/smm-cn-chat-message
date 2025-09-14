package org.acme.chat.application.in.chatsidebarexample;

import java.util.List;

import org.acme.shared.ChatSidebar.ChatSidebarDTO;

import io.smallrye.mutiny.Uni;

public interface GetPsychologistSidebarAllUseCase {
     Uni<List<ChatSidebarDTO>> execute(String psychologistUserId);
    
}
