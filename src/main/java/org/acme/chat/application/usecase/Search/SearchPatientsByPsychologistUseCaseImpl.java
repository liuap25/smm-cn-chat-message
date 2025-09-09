package org.acme.chat.application.usecase.Search;

import java.util.List;
import java.util.stream.Collectors;

import org.acme.chat.application.in.Search.SearchPatientsByPsychologistUseCase;
import org.acme.chat.application.out.PsychologistClientPort;
import org.acme.shared.Search.PatientSearchResultDTO;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SearchPatientsByPsychologistUseCaseImpl  implements SearchPatientsByPsychologistUseCase{

    
    @Inject
    PsychologistClientPort psychologistClient;

    @Override
    public Uni<List<PatientSearchResultDTO>> searchPatients(String psychologistUserId, String searchQuery)  {
        return psychologistClient
                .getPatientsByPsychologist(psychologistUserId) 
                .map(patients -> patients.stream()
                        .filter(p -> p.getFullName().toLowerCase().contains(searchQuery.toLowerCase()))
                        .map(p -> new PatientSearchResultDTO(p.getUserId(), p.getFullName()))
                        .collect(Collectors.toList())
                );
    }

    
}
