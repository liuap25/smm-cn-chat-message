package org.acme.chat.application.usecase.chatsidebar.Patient;

import java.util.List;
import java.util.stream.Collectors;

import org.acme.chat.application.in.chatsidebar.GetChatGroupsForPatientUseCase;
import org.acme.chat.application.out.ChatGroupRepositoryPort;
import org.acme.chat.application.out.PsychologistClientPort;
import org.acme.shared.PsychologistDTO;
import org.acme.shared.ChatSidebar.ChatSidebarDTO;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetChatGroupsForPatientUseCaseImpl implements GetChatGroupsForPatientUseCase {

    @Inject
    ChatGroupRepositoryPort chatGroupRepository;

    @Inject
    PsychologistClientPort psychologistClient;

    @Override
    public Uni<List<ChatSidebarDTO>> getChatGroupsByPatientId(String patientId) {
        return chatGroupRepository.findChatGroupsByUserId(patientId)
            .onItem().transformToUni(chatGroups ->
                psychologistClient.getPsychologistsByPatient(patientId)
                    .onItem().transformToUni(psychologists -> {

                        var sidebarUnis = chatGroups.stream()
                            .map(group -> {
                                // Determinar el otro usuario en la conversación (psicólogo)
                                String otherUserId = group.psychologistId();

                                // Buscar perfil del psicólogo
                                PsychologistDTO profile = psychologists.stream()
                                    .filter(p -> p.id().equals(otherUserId))
                                    .findFirst()
                                    .orElse(new PsychologistDTO(
                                        otherUserId,
                                        "Psicólogo desconocido",
                                        null
                                    ));

                                // Obtener último mensaje y contar mensajes no leídos
                                return chatGroupRepository.findLastMessage(group.id())
                                    .onItem().transformToUni(lastMessage ->
                                        chatGroupRepository.countUnreadMessages(group.id(), patientId)
                                            .onItem().transform(unreadCount -> new ChatSidebarDTO(
                                                group.id(),
                                                otherUserId,
                                                profile.fullName(),
                                                profile.photoUrl(),
                                                lastMessage != null ? lastMessage.getMessage() : "",
                                                lastMessage != null ? lastMessage.getSentAt() : null,
                                                unreadCount
                                            ))
                                    );
                            })
                            .collect(Collectors.toList());

                        if (sidebarUnis.isEmpty()) {
                            return Uni.createFrom().item(List.of());
                        }

                        // Combinar todos los Uni<ChatSidebarDTO> en Uni<List<ChatSidebarDTO>>
                        return Uni.combine().all().unis(sidebarUnis)
                            .combinedWith(list -> list.stream()
                                .map(obj -> (ChatSidebarDTO) obj)
                                .toList()
                            );
                    })
            );
    }
    
}
