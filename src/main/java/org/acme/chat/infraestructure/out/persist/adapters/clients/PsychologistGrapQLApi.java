package org.acme.chat.infraestructure.out.persist.adapters.clients;

import java.util.List;

import org.acme.shared.PatientProfileChatOut;
import org.acme.shared.PsychologistChatDto;
import org.acme.shared.PsychologistDTO;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import io.smallrye.mutiny.Uni;


@GraphQLClientApi(configKey = "psychologist-api")
public interface PsychologistGrapQLApi {

    @Query("GetAllPatientsByPsychologo")
    Uni<List<PatientProfileChatOut>> getPatientProfilesForChat(@Name("psychologistExternalId")String psychologistExternalId);

    @Query("GetAllPsychologistByPatient")
    Uni<List<PsychologistDTO>> getPsychologistsByPatient(@Name("patientId")String patientId);

    @Query("GetPsychologistChatByUserId")
    Uni<PsychologistChatDto> getPsychologistByUserId(
        @Name("userId") String userId
    );

    @Query("findPsychologistProfileId")
    Uni<String> findPsychologistProfileId(@Name("userId") String userId);

    @Query("getPatientsByPsychologist")
    Uni<List<PatientProfileChatOut>> getPatientsByPsychologist(@Name("psychologistUserId") String psychologistUserId);

}
