package org.acme.chat.application.usecase.Search;

import java.util.List;
import java.util.stream.Collectors;

import org.acme.chat.application.in.Search.SearchPsychologistsByPatientUseCase;
import org.acme.chat.application.out.PsychologistClientPort;
import org.acme.shared.Search.PsychologistSearchResultDTO;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@ApplicationScoped
public class SearchPsychologistsByPatientUseCaseImpl implements SearchPsychologistsByPatientUseCase {

    @Inject
    PsychologistClientPort psychologistClient;

    @Override
    public Uni<List<PsychologistSearchResultDTO>> searchPsychologists(String patientId, String searchQuery) {
           return psychologistClient
            .getPsychologistsByPatient(patientId) 
            .map(psychologists -> psychologists.stream()   
                    .filter(p -> p.fullName().toLowerCase().contains(searchQuery.toLowerCase()))
                    .map(p -> new PsychologistSearchResultDTO(p.id(), p.fullName()))
                    .collect(Collectors.toList())
            );
    
    }

    
}
