package org.acme.chat.infraestructure.out.persist.adapters.clients;



import org.acme.chat.application.out.PatientClientPort;
import org.acme.shared.PatientChatDto;


import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PatientGraphQLClient implements PatientClientPort{

    @Inject
    PatientGraphQLApi graphQLApi;


    @Override
    public Uni<PatientChatDto> getChatPatient(String userId) { 
         return graphQLApi.getChatPatient(userId);
    }


    
    


}
