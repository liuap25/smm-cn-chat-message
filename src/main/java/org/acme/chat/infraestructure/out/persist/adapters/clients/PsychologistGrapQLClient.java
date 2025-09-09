package org.acme.chat.infraestructure.out.persist.adapters.clients;

import java.util.List;

import org.acme.chat.application.out.PsychologistClientPort;
import org.acme.shared.PatientProfileChatOut;
import org.acme.shared.PsychologistChatDto;
import org.acme.shared.PsychologistDTO;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PsychologistGrapQLClient implements PsychologistClientPort {

    @Inject
    PsychologistGrapQLApi psychologistGrapQLApi;


    @Override
    public Uni<List<PatientProfileChatOut>> getPatientProfilesForChat(String psychologistExternalId) {
     return psychologistGrapQLApi.getPatientProfilesForChat(psychologistExternalId);
    }

    @Override
    public Uni<List<PsychologistDTO>> getPsychologistsByPatient(String patientId) {  
        return psychologistGrapQLApi.getPsychologistsByPatient(patientId);
    }

    @Override
    public Uni<PsychologistChatDto> getPsychologistByUserId(String userId) {
        return psychologistGrapQLApi.getPsychologistByUserId(userId);
    }

     @Override
    public Uni<String> findPsychologistProfileId(String userId) {
        return psychologistGrapQLApi.findPsychologistProfileId(userId);
    }

    @Override
    public Uni<List<PatientProfileChatOut>> getPatientsByPsychologist(String psychologistUserId) {
        return psychologistGrapQLApi.getPatientsByPsychologist(psychologistUserId);
    }




    

    
 


       
    
}
