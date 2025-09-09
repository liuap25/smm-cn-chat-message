package org.acme.chat.application.usecase;

import java.util.List;
import java.util.stream.Collectors;

import org.acme.chat.application.in.GetPsychologistsForPatientUseCase;
import org.acme.chat.application.out.ChatGroupRepositoryPort;
import org.acme.chat.application.out.PsychologistClientPort;
import org.acme.shared.PsychologistDTO;
import org.acme.shared.ChatSidebar.ChatSidebarDTO;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetPsychologistForPatientUseCaseImpl implements GetPsychologistsForPatientUseCase {

    @Inject
    ChatGroupRepositoryPort chatGroupRepository;

    @Inject
    PsychologistClientPort psychologistClient;

    @Override
    public Uni<List<ChatSidebarDTO>> getPsychologistsForPatient(String patientId) {
        return chatGroupRepository.findChatGroupsByUserId(patientId)
            .onItem().transformToUni(chatGroups ->
                psychologistClient.getPsychologistsByPatient(patientId)
                    .onItem().transformToUni(psychologistProfiles -> {

                        var sidebarUnis = chatGroups.stream()
                            .map(group -> {
                                // Determinamos el otro usuario: el psicólogo
                                String otherUserId = group.psychologistId();

                                // Buscamos el perfil del psicólogo
                                PsychologistDTO profile = psychologistProfiles.stream()
                                    .filter(p -> p.id().equals(otherUserId))
                                    .findFirst()
                                    .orElse(new PsychologistDTO(
                                        otherUserId,
                                        "Usuario desconocido",
                                        null
                                    ));

                                // Obtenemos el último mensaje del chat
                                return chatGroupRepository.findLastMessage(group.id())
                                    .onItem().transform(lastMessage -> new ChatSidebarDTO(
                                        group.id(),
                                        otherUserId,
                                        profile.fullName(),
                                        profile.photoUrl(),
                                        lastMessage != null ? lastMessage.getMessage() : "",
                                        lastMessage != null ? lastMessage.getSentAt() : null,
                                        0 // Mensajes no leídos (pendiente)
                                    ));
                            })
                            .collect(Collectors.toList());

                        // Combinamos todos los Uni<ChatSidebarDTO> en Uni<List<ChatSidebarDTO>>
                        return Uni.combine().all().unis(sidebarUnis)
                            .combinedWith(list -> list.stream()
                                .map(obj -> (ChatSidebarDTO) obj)
                                .toList()
                            );
                    })
            );
    }
}