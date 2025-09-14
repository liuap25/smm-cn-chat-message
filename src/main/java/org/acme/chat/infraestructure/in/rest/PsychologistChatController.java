package org.acme.chat.infraestructure.in.rest;

import java.util.List;

import org.acme.chat.application.out.PsychologistClientPort;
import org.acme.shared.PatientProfileChatOut;
import org.acme.shared.PsychologistChatDto;
import org.acme.shared.PsychologistDTO;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/psychologist")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PsychologistChatController {

    @Inject
    PsychologistClientPort psychologistClient;

   
    @GET
    @Path("/getallpsychologist/{patientId}")
    public Uni<List<PsychologistDTO>> getPsychologistsByPatient(@PathParam("patientId") String patientId ){
        return psychologistClient.getPsychologistsByPatient(patientId);
     }

    @GET
    @Path("/{userId}")
    public Uni<PsychologistChatDto> getPsychologistChatByUserId(@PathParam("userId") String userId) {
        return psychologistClient.getPsychologistByUserId(userId);
    }

    @GET
    @Path("/patients/{psychologistUserId}")
    public Uni<List<PatientProfileChatOut>> getPatientsByPsychologist(@PathParam("psychologistUserId") String psychologistUserId) {
        return psychologistClient.getPatientsByPsychologist(psychologistUserId);
    }
   



}
