package org.acme.chat.infraestructure.in.graphql;

import java.util.List;

import org.acme.chat.application.in.Search.SearchPatientsByPsychologistUseCase;
import org.acme.chat.application.in.Search.SearchPsychologistsByPatientUseCase;
import org.acme.shared.Search.PatientSearchResultDTO;
import org.acme.shared.Search.PsychologistSearchResultDTO;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@GraphQLApi
@ApplicationScoped
public class SearchGraphQLController{

    @Inject
    SearchPatientsByPsychologistUseCase searchPatientsUseCase;

    @Inject
    SearchPsychologistsByPatientUseCase searchPsychologistsUseCase;


    /**
     * Query GraphQL para buscar pacientes por nombre de psicólogo.
     * 
     * @param psychologistUserId El ID del psicólogo
     * @param query Parte o todo del nombre del paciente
     * @return Uni con lista de pacientes que coinciden
     */
    @Query("SearchPatients")
     public Uni<List<PatientSearchResultDTO>> searchPatients(String psychologistUserId, String query) {
        return searchPatientsUseCase.searchPatients(psychologistUserId, query);
    }


     /**
     * Query GraphQL para buscar psicólogos de un paciente por nombre.
     *
     * @param patientId ID del paciente
     * @param query Parte o todo del nombre del psicólogo
     * @return Uni con lista de psicólogos que coinciden
     */
    @Query("searchPsychologists")
    public Uni<List<PsychologistSearchResultDTO>> searchPsychologists(String patientId, String query) {
        // Llamamos al UseCase 100% reactivo
        return searchPsychologistsUseCase.searchPsychologists(patientId, query);
    }
    
}
