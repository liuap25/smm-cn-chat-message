package org.acme.chat.infraestructure.in.rest;



import org.acme.chat.application.out.PatientClientPort;
import org.acme.shared.PatientChatDto;


import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/patient")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PatientChatController {

    @Inject
    PatientClientPort patientClient;

    @GET
    @Path("/{userId}")
    public Uni<PatientChatDto> testClient(@PathParam("userId") String userId) {
        return patientClient.getChatPatient(userId);
    }

 
}
