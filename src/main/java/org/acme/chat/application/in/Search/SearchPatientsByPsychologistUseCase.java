package org.acme.chat.application.in.Search;

import java.util.List;

import org.acme.shared.Search.PatientSearchResultDTO;

import io.smallrye.mutiny.Uni;

public interface SearchPatientsByPsychologistUseCase {
     Uni<List<PatientSearchResultDTO>> searchPatients(String psychologistUserId, String searchQuery);
    
}
