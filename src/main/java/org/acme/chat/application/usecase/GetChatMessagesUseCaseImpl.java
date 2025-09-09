package org.acme.chat.application.usecase;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.acme.chat.application.in.GetChatMessagesUseCase;
import org.acme.chat.application.out.ChatGroupRepositoryPort;
import org.acme.chat.application.out.ChatMessageRepositoryPort;
import org.acme.chat.application.out.PatientClientPort;
import org.acme.chat.application.out.PsychologistClientPort;
import org.acme.shared.GetMessages.ChatConversationDTO;
import org.acme.shared.GetMessages.ChatMessageDTO;
import org.acme.shared.GetMessages.ChatUserDTO;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetChatMessagesUseCaseImpl implements GetChatMessagesUseCase {

    @Inject
    ChatMessageRepositoryPort chatMessageRepository;

    @Inject
    PsychologistClientPort psychologistClient;

    @Inject
    PatientClientPort patientClient;

    @Inject
    ChatGroupRepositoryPort chatGroupRepository;

    @Override
    public Uni<ChatConversationDTO> getMessagesByChatGroup(String chatGroupId, String currentUserId,int offset,int limit) {
            
        return chatGroupRepository.findById(chatGroupId)
        .onItem().transformToUni(chatGroup ->
            chatMessageRepository.findAllByChatGroupId(chatGroupId, offset, limit)
                .onItem().transformToUni(messages -> {

                    if (messages.isEmpty()) {
                        return Uni.createFrom().item(new ChatConversationDTO(null, List.of()));
                    }

                    // Determinar el otro usuario en base al chatGroup
                    String otherUserId = chatGroup.psychologistId().equals(currentUserId)
                            ? chatGroup.patientId()
                            : chatGroup.psychologistId();

                    boolean isOtherPsychologist = chatGroup.psychologistId().equals(otherUserId);

                    // Obtenemos los datos del otro usuario
                    Uni<ChatUserDTO> otherUserUni = isOtherPsychologist
                        ? psychologistClient.getPsychologistByUserId(otherUserId)
                            .onItem().transform(dto -> new ChatUserDTO(
                                otherUserId,
                                dto != null && dto.getFullName() != null ? dto.getFullName() : "Psicólogo desconocido",
                                dto != null && dto.getPhotoUrl() != null ? dto.getPhotoUrl() : "default-photo.png"
                            ))
                        : patientClient.getChatPatient(otherUserId)
                            .onItem().transform(dto -> new ChatUserDTO(
                                otherUserId,
                                dto != null && dto.getFullname() != null ? dto.getFullname() : "Paciente desconocido",
                                dto != null && dto.getPhotoUrl() != null ? dto.getPhotoUrl() : "default-photo.png"
                            ));

                    // Cache para evitar llamadas repetitivas
                    Map<String, Uni<List<String>>> cache = new ConcurrentHashMap<>();

                    // Convertimos los mensajes a ChatMessageDTO
                    var messageUnis = messages.stream().map(message -> {

                        boolean isSenderCurrentUser = message.senderId().equals(currentUserId);

                        if (isSenderCurrentUser) {
                            return Uni.createFrom().item(new ChatMessageDTO(
                                message.id(),
                                message.message(),
                                message.sentAt(),
                                message.senderId(),
                                message.receiverId()
                            ));
                        } else {
                            var cached = cache.computeIfAbsent(message.senderId(), id -> {
                                boolean isSenderPsychologist = chatGroup.psychologistId().equals(id);
                                return isSenderPsychologist
                                    ? psychologistClient.getPsychologistByUserId(id)
                                        .onItem().transform(dto -> List.of(
                                            dto != null && dto.getFullName() != null ? dto.getFullName() : "Psicólogo desconocido",
                                            dto != null && dto.getPhotoUrl() != null ? dto.getPhotoUrl() : "default-photo.png"
                                        ))
                                    : patientClient.getChatPatient(id)
                                        .onItem().transform(dto -> List.of(
                                            dto != null && dto.getFullname() != null ? dto.getFullname() : "Paciente desconocido",
                                            dto != null && dto.getPhotoUrl() != null ? dto.getPhotoUrl() : "default-photo.png"
                                        ));
                            });

                            return cached.onItem().transform(data -> new ChatMessageDTO(
                                message.id(),
                                message.message(),
                                message.sentAt(),
                                message.senderId(),
                                message.receiverId(),
                                data.get(0), // senderFullName
                                data.get(1)  // senderPhotoUrl
                            ));
                        }
                    }).collect(Collectors.toList());

                    return Uni.combine().all().unis(
                        otherUserUni,
                        Uni.combine().all().unis(messageUnis)
                            .with(results -> results.stream()
                                .map(result -> (ChatMessageDTO) result)
                                .collect(Collectors.toList())
                            )
                    ).with((otherUser, messagesList) -> new ChatConversationDTO(otherUser, messagesList));
                })
        );
    }
}
        
    
