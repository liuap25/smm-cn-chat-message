package org.acme.chat.application.out;

import java.util.List;
import org.acme.shared.PatientProfileChatOut;
import org.acme.shared.PsychologistChatDto;
import org.acme.shared.PsychologistDTO;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface PsychologistClientPort {

    Uni<List<PsychologistDTO>> getPsychologistsByPatient(String patientId);
    Uni<PsychologistChatDto> getPsychologistByUserId(String userId);
    Uni<List<PatientProfileChatOut>> getPatientsByPsychologist(String psychologistUserId);

   


    
}
