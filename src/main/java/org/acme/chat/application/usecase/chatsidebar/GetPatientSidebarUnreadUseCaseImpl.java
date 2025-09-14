package org.acme.chat.application.usecase.chatsidebar;

import java.util.List;
import java.util.stream.Collectors;

import org.acme.chat.application.in.chatsidebarexample.GetPatientSidebarUnreadUseCase;
import org.acme.chat.infraestructure.out.persist.adapters.ChatSidebarRepositoryAdapter;
import org.acme.shared.ChatSidebar.ChatSidebarDTO;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetPatientSidebarUnreadUseCaseImpl implements GetPatientSidebarUnreadUseCase {

     @Inject
    ChatSidebarRepositoryAdapter repository;

    @Override
    public Uni<List<ChatSidebarDTO>> execute(String patientUserId) {
        return repository.findChatSidebarData(patientUserId)
                .onItem().transform(list ->
                    list.stream()
                        .filter(chat -> chat.getUnreadCount() > 0)
                        .collect(Collectors.toList())
                );
    }


    
}
