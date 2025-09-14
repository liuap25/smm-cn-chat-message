package org.acme.chat.infraestructure.in.graphql;

import java.util.List;

import org.acme.chat.application.in.chatsidebarexample.GetPatientSidebarAllUseCase;
import org.acme.chat.application.in.chatsidebarexample.GetPatientSidebarUnreadUseCase;
import org.acme.chat.application.in.chatsidebarexample.GetPsychologistSidebarAllUseCase;
import org.acme.chat.application.in.chatsidebarexample.GetPsychologistSidebarUnreadUseCase;
import org.acme.chat.infraestructure.out.event.ChatSidebarSubscriber;
import org.acme.shared.ChatSidebar.ChatSidebarDTO;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

import io.smallrye.graphql.api.Subscription;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@GraphQLApi
public class ChatStaticSidebarController {

    @Inject
    ChatSidebarSubscriber sidebarSubscriber;

    @Inject
    GetPsychologistSidebarAllUseCase getPsychologistSidebarAll;

    @Inject
    GetPsychologistSidebarUnreadUseCase getPsychologistSidebarUnread;

    @Inject
    GetPatientSidebarAllUseCase getPatientSidebarAll;

    @Inject
    GetPatientSidebarUnreadUseCase getPatientSidebarUnread;




    // Psicólogo
    @Query("getPsychologistSidebarAll")
    public Uni<List<ChatSidebarDTO>> getPsychologistSidebarAll(String userId) {
        return getPsychologistSidebarAll.execute(userId);
    }

    @Query("getPsychologistSidebarUnread")
    public Uni<List<ChatSidebarDTO>> getPsychologistSidebarUnread(String userId) {
        return getPsychologistSidebarUnread.execute(userId);
    }

    // Paciente
    @Query("getPatientSidebarAll")
    public Uni<List<ChatSidebarDTO>> getPatientSidebarAll(String userId) {
        return getPatientSidebarAll.execute(userId);
    }

    @Query("getPatientSidebarUnread")
    public Uni<List<ChatSidebarDTO>> getPatientSidebarUnread(String userId) {
        return getPatientSidebarUnread.execute(userId);
    }

       

// ----------------- Psicólogo -----------------
@Subscription
public Multi<ChatSidebarDTO> psychologistSidebarAll(String psychologistUserId) {
    return sidebarSubscriber.getPsychologistAllSidebarUpdates()
            .filter(dto -> dto.getOtherUserId() != null)
            .filter(dto -> !dto.getOtherUserId().equals(psychologistUserId)); 
}

@Subscription
public Multi<ChatSidebarDTO> psychologistSidebarUnread(String psychologistUserId) {
    return sidebarSubscriber.getPsychologistUnreadSidebarUpdates()
            .filter(dto -> dto.getOtherUserId() != null)
            .filter(dto -> !dto.getOtherUserId().equals(psychologistUserId))
            .filter(dto -> dto.getUnreadCount() > 0);
}

// ----------------- Paciente -----------------
@Subscription
public Multi<ChatSidebarDTO> patientSidebarAll(String patientUserId) {
    return sidebarSubscriber.getPatientAllSidebarUpdates()
            .filter(dto -> dto.getOtherUserId() != null)
            .filter(dto -> !dto.getOtherUserId().equals(patientUserId));
}

@Subscription
public Multi<ChatSidebarDTO> patientSidebarUnread(String patientUserId) {
    return sidebarSubscriber.getPatientUnreadSidebarUpdates()
            .filter(dto -> dto.getOtherUserId() != null)
            .filter(dto -> !dto.getOtherUserId().equals(patientUserId))
            .filter(dto -> dto.getUnreadCount() > 0);
}

    
}
