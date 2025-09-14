package org.acme.chat.application.in.chatsidebarexample;

import java.util.List;

import org.acme.shared.ChatSidebar.ChatSidebarDTO;

import io.smallrye.mutiny.Uni;

public interface GetPatientSidebarUnreadUseCase {
    Uni<List<ChatSidebarDTO>> execute(String patientUserId);
    
}
