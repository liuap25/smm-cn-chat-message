package org.acme.chat.domain.model;

import java.time.Instant;

public record ChatMessageIncident(
        String id,              
        String chatMessageId,   
        String originalMessage, 
        Instant detectedAt      

) {
   
}
