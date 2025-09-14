package org.acme.chat.infraestructure.out.persist.adapters;

import java.time.Instant;
import java.util.List;

import org.acme.chat.application.out.ChatSidebarRepositoryPort;
import org.acme.chat.infraestructure.out.persist.adapters.clients.PatientGraphQLClient;
import org.acme.chat.infraestructure.out.persist.adapters.clients.PsychologistGrapQLClient;
import org.acme.chat.infraestructure.out.persist.repository.ChatGroupRepository;
import org.acme.shared.ChatSidebar.ChatSidebarDTO;
import org.acme.shared.ChatSidebar.ChatSidebarRawDTO;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ChatSidebarRepositoryAdapter implements ChatSidebarRepositoryPort {

    @Inject
    ChatGroupRepository chatGroupRepository;

    @Inject
    PsychologistGrapQLClient psychologistClient;

    @Inject
    PatientGraphQLClient patientClient;

    @Override
    public Uni<List<ChatSidebarRawDTO>> findChatSidebarDataExample(String userId) {
    return chatGroupRepository.findChatSidebarRawData(userId)
        .map(results -> results.stream()
            .map(row -> {
                String chatGroupId = row[0] != null ? row[0].toString() : null;
                String otherUserId = row[1] != null ? row[1].toString() : null;
                String lastMessageDate = row[2] != null ? row[2].toString() : null;
                String lastMessage = row[3] != null ? row[3].toString() : null;
                int unreadCount = row[4] != null ? ((Number) row[4]).intValue() : 0;

                return new ChatSidebarRawDTO(chatGroupId, otherUserId, lastMessage, lastMessageDate, unreadCount);
            })
            .toList()
        );
        }

    @Override
    public Uni<List<ChatSidebarDTO>> findChatSidebarData(String userId) {
          return chatGroupRepository.findChatSidebarRawData(userId)
        .onItem().transformToUni(rows -> {
            List<Uni<ChatSidebarDTO>> dtoUnis = rows.stream()
                .map(row -> {
                    String chatGroupId = row[0].toString();
                    String otherUserId = row[1].toString();
                    Instant lastMessageDate = row[2] != null ? Instant.parse(row[2].toString()) : null;
                    String lastMessage = row[3] != null ? row[3].toString() : null;
                    long unreadCount = row[4] != null ? ((Number) row[4]).longValue() : 0;

                    return psychologistClient.getPsychologistByUserId(otherUserId)
                        .onFailure().recoverWithItem(() -> null) 
                        .onItem().transformToUni(psych -> {
                            if (psych != null) {
                                return Uni.createFrom().item(new ChatSidebarDTO(
                                    chatGroupId,
                                    otherUserId,
                                    psych.getFullName() != null ? psych.getFullName() : "",
                                    psych.getPhotoUrl() != null ? psych.getPhotoUrl() : "",
                                    lastMessage,
                                    lastMessageDate,
                                    unreadCount
                                ));
                            }
                            return patientClient.getChatPatient(otherUserId)
                                .onFailure().recoverWithItem(() -> null)
                                .onItem().transform(patient -> new ChatSidebarDTO(
                                    chatGroupId,
                                    otherUserId,
                                    patient != null && patient.getFullname() != null ? patient.getFullname() : "",
                                    patient != null && patient.getPhotoUrl() != null ? patient.getPhotoUrl() : "",
                                    lastMessage,
                                    lastMessageDate,
                                    unreadCount
                                ));
                        });
                }).toList();

            return Uni.combine().all().unis(dtoUnis)
                .combinedWith(list -> list.stream()
                    .map(item -> (ChatSidebarDTO) item)
                    .toList()
                );
        });
    }
}

    



    

