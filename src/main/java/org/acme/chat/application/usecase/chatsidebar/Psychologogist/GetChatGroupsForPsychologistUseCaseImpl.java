package org.acme.chat.application.usecase.chatsidebar.Psychologogist;

import java.util.List;
import java.util.stream.Collectors;

import org.acme.chat.application.in.GetChatGroupsForPsychologistUseCase;
import org.acme.chat.application.out.ChatGroupRepositoryPort;
import org.acme.chat.application.out.PsychologistClientPort;
import org.acme.shared.PatientProfileChatOut;
import org.acme.shared.ChatSidebar.ChatSidebarDTO;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetChatGroupsForPsychologistUseCaseImpl implements GetChatGroupsForPsychologistUseCase{

    @Inject
    ChatGroupRepositoryPort chatGroupRepository;

    @Inject
    PsychologistClientPort psychologistClient;

    @Override
    public Uni<List<ChatSidebarDTO>> getChatGroupsByPsychologistId(String psychologistId) {
         return chatGroupRepository.findChatGroupsByUserId(psychologistId)
        .onItem().transformToUni(chatGroups ->
            psychologistClient.getPatientsByPsychologist(psychologistId)
                .onItem().transformToUni(patientProfiles -> {

                    var sidebarUnis = chatGroups.stream()
                        .map(group -> {
                            // Determinar el otro usuario en la conversación
                            String otherUserId = group.psychologistId().equals(psychologistId)
                                ? group.patientId()
                                : group.psychologistId();

                            // Buscar perfil del otro usuario
                            PatientProfileChatOut profile = patientProfiles.stream()
                                .filter(p -> p.getUserId().equals(otherUserId))
                                .findFirst()
                                .orElse(new PatientProfileChatOut(
                                    otherUserId,
                                    "Usuario desconocido",
                                    null
                                ));

                            // Obtener último mensaje y contar mensajes no leídos
                            return chatGroupRepository.findLastMessage(group.id())
                                .onItem().transformToUni(lastMessage ->
                                    chatGroupRepository.countUnreadMessages(group.id(), psychologistId)
                                        .onItem().transform(unreadCount -> new ChatSidebarDTO(
                                            group.id(),
                                            otherUserId,
                                            profile.getFullName(),
                                            profile.getPhotoUrl(),
                                            lastMessage != null ? lastMessage.getMessage() : "",
                                            lastMessage != null ? lastMessage.getSentAt() : null,
                                            unreadCount // ✅ Ahora sí mostramos mensajes no leídos
                                        ))
                                );
                        })
                        .collect(Collectors.toList());

                    // Si no hay chatGroups, devolvemos lista vacía
                    if (sidebarUnis.isEmpty()) {
                        return Uni.createFrom().item(List.of());
                    }

                    // Combinamos todos los Uni<ChatSidebarDTO> en un Uni<List<ChatSidebarDTO>>
                    return Uni.combine().all().unis(sidebarUnis)
                        .combinedWith(list -> list.stream()
                            .map(obj -> (ChatSidebarDTO) obj)
                            .toList()
                        );
                })
        );
    }
          

}


    


    

