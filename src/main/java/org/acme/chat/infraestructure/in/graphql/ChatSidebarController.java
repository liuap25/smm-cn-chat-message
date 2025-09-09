package org.acme.chat.infraestructure.in.graphql;

import java.util.List;

import org.acme.chat.application.in.GetChatGroupsForPsychologistUseCase;
import org.acme.chat.application.in.GetPsychologistsForPatientUseCase;
import org.acme.chat.application.in.chatsidebar.GetUnreadChatGroupsForPsychologistUseCase;
import org.acme.chat.infraestructure.out.event.ChatSidebarSubscriber;
import org.acme.shared.ChatSidebar.ChatSidebarDTO;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import io.smallrye.graphql.api.Subscription;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@GraphQLApi
@ApplicationScoped
public class ChatSidebarController {

    @Inject
    GetChatGroupsForPsychologistUseCase  getChatGroupsForPsychologistUseCase;

    @Inject
    GetUnreadChatGroupsForPsychologistUseCase getUnreadChatGroupsForPsychologistUseCase;

    
    @Inject
    ChatSidebarSubscriber sidebarSubscriber;







   


    @Inject
    GetPsychologistsForPatientUseCase getPsychologistsForPatientUseCase;

    @Query("getChatGroupsForPsychologist")
    @Description("Obtiene la lista de chats de un psicólogo")
    public Uni<List<ChatSidebarDTO>> getChatGroupsForPsychologist(String psychologistId) {
        return  getChatGroupsForPsychologistUseCase.getChatGroupsByPsychologistId(psychologistId);
    }

    

    @Query("psychologistsForPatient")
    public Uni<List<ChatSidebarDTO>> getPsychologistsForPatient(
            @Name("patientId") String patientId) {
        return getPsychologistsForPatientUseCase.getPsychologistsForPatient(patientId);
    }



    @Subscription
    @Description("Se suscribe a las actualizaciones del chat sidebar para un usuario específico")
    public Multi<List<ChatSidebarDTO>> chatSidebarUpdate(String psychologistId) {
        return sidebarSubscriber.getSidebarUpdates()
        .onItem().transformToUni(update -> 
            getChatGroupsForPsychologistUseCase.getChatGroupsByPsychologistId(psychologistId)
        )
        .concatenate(); 
    }


    /**
     * Obtiene todos los chat groups que contienen mensajes no leídos
     * para un psicólogo específico.
     */
    @Query("getUnreadChatGroupsForPsychologist")
    @Description("Obtiene los chat groups con mensajes no leídos para un psicólogo")
    public Uni<List<ChatSidebarDTO>> getUnreadChatGroupsForPsychologist(String psychologistUserId) {
        return getUnreadChatGroupsForPsychologistUseCase.getUnreadChatGroupsForPsychologist(psychologistUserId);
    }

 
   

    




 





}
