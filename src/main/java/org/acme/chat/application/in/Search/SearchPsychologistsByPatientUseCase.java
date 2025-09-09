package org.acme.chat.application.in.Search;

import java.util.List;

import org.acme.shared.Search.PsychologistSearchResultDTO;

import io.smallrye.mutiny.Uni;

public  interface SearchPsychologistsByPatientUseCase {
     Uni<List<PsychologistSearchResultDTO>> searchPsychologists(String patientId, String searchQuery);

    
}
