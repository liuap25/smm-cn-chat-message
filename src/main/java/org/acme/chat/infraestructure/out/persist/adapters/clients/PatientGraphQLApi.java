package org.acme.chat.infraestructure.out.persist.adapters.clients;


import org.acme.shared.PatientChatDto;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import io.smallrye.mutiny.Uni;

@GraphQLClientApi(configKey = "patient-api")
public interface PatientGraphQLApi {
    
    @Query("getChatPatient")
    Uni<PatientChatDto> getChatPatient(@Name("userId") String userId);


    
}
