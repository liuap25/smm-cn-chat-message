package org.acme.chat.application.usecase.chatsidebar.Psychologogist;

import java.util.List;
import java.util.stream.Collectors;

import org.acme.chat.application.in.chatsidebar.GetUnreadChatGroupsForPsychologistUseCase;
import org.acme.chat.application.out.ChatGroupRepositoryPort;
import org.acme.chat.application.out.PsychologistClientPort;
import org.acme.chat.infraestructure.out.event.ChatMessagePublisher;
import org.acme.shared.PatientProfileChatOut;
import org.acme.shared.ChatSidebar.ChatSidebarDTO;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetUnreadChatGroupsForPsychologistUseCaseImpl implements GetUnreadChatGroupsForPsychologistUseCase{

    @Inject
    ChatGroupRepositoryPort chatGroupRepository;

    @Inject
    PsychologistClientPort psychologistClient;

     @Inject
    ChatMessagePublisher publisher;

    @Override
    public Uni<List<ChatSidebarDTO>> getUnreadChatGroupsForPsychologist(String psychologistUserId) {
        return chatGroupRepository.findUnreadChatGroupsByUserId(psychologistUserId)
    .onItem().transformToUni(chatGroups ->
        psychologistClient.getPatientsByPsychologist(psychologistUserId)
            .onItem().transformToUni(patientProfiles -> {

                var sidebarUnis = chatGroups.stream()
                    .map(group -> {
                        String otherUserId = group.psychologistId().equals(psychologistUserId)
                            ? group.patientId()
                            : group.psychologistId();

                        PatientProfileChatOut profile = patientProfiles.stream()
                            .filter(p -> p.getUserId().equals(otherUserId))
                            .findFirst()
                            .orElse(new PatientProfileChatOut(
                                otherUserId,
                                "Usuario desconocido",
                                null
                            ));

                        return chatGroupRepository.findLastMessage(group.id())
                            .onItem().transformToUni(lastMessage ->
                                // PASAR EL ID DEL PSICÓLOGO COMO RECEPTOR
                                chatGroupRepository.countUnreadMessages(group.id(), psychologistUserId)
                                    .onItem().transform(count -> {
                                        ChatSidebarDTO dto = new ChatSidebarDTO(
                                            group.id(),
                                            otherUserId,
                                            profile.getFullName(),
                                            profile.getPhotoUrl(),
                                            lastMessage != null ? lastMessage.getMessage() : "",
                                            lastMessage != null ? lastMessage.getSentAt() : null,
                                            count
                                        );

                                        // Publicación en tiempo real
                                        publisher.publishSidebarUpdate(dto);
                                        return dto;
                                    })
                            );
                    })
                    .collect(Collectors.toList());

                if (sidebarUnis.isEmpty()) {
                    return Uni.createFrom().item(List.of());
                }

                return Uni.combine().all().unis(sidebarUnis)
                    .combinedWith(list -> list.stream()
                        .map(obj -> (ChatSidebarDTO) obj)
                        .toList()
                    );
            })
    );
    }
}

    


    

