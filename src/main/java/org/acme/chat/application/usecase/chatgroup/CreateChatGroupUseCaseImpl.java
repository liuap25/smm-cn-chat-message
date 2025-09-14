package org.acme.chat.application.usecase.chatgroup;

import java.time.Instant;

import org.acme.chat.application.in.chatgroup.CreateChatGroupUseCase;
import org.acme.chat.application.out.ChatGroupRepositoryPort;
import org.acme.chat.application.out.PatientClientPort;
import org.acme.chat.application.out.PsychologistClientPort;
import org.acme.chat.domain.exception.ChatGroupCreationException;
import org.acme.chat.domain.model.ChatGroup;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CreateChatGroupUseCaseImpl implements CreateChatGroupUseCase {
    @Inject
    ChatGroupRepositoryPort chatGroupRepository;

    @Inject
    PatientClientPort patientClient;

    @Inject
    PsychologistClientPort psychologistClient;

    @Override
    public Uni<ChatGroup> createChatGroup(String psychologistId, String patientId) {
        
          
    Uni<Boolean> patientBelongsToPsychologist = 
    psychologistClient.getPatientsByPsychologist(psychologistId)
        .onItem().transform(patients -> 
            patients.stream()
                    .anyMatch(p -> p.getUserId().equals(patientId))
        );

    Uni<Boolean> psychologistBelongsToPatient =
    psychologistClient.getPsychologistsByPatient(patientId)
        .onItem().transform(psychos -> 
            psychos.stream()
                .anyMatch(ps -> ps.id().equals(psychologistId))
        );

    return Uni.combine().all().unis(patientBelongsToPsychologist, psychologistBelongsToPatient)
        .with((patientCheck, psychoCheck) -> patientCheck && psychoCheck)
        .onItem().transformToUni(isRelated -> {
            if (!isRelated) {
                return Uni.createFrom().failure(
                    new ChatGroupCreationException("No existe relación bidireccional entre psicólogo y paciente.")
                );
            }

            return chatGroupRepository.findByPsychologistIdAndPatientId(psychologistId, patientId)
                .onItem().transformToUni(existing -> {
                    if (existing != null) {
                        return Uni.createFrom().failure(
                            new ChatGroupCreationException("Ya existe un chat entre este psicólogo y paciente.")
                        );
                    }

                    
                    ChatGroup chatGroup = new ChatGroup(
                        null,
                        psychologistId,
                        patientId,
                        Instant.now(),
                        Instant.now()
                    );

                    return chatGroupRepository.save(chatGroup);
                });
        });
    }
}




        
        
    

    

    

