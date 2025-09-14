package org.acme.shared.ChatMessage;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponseDto {
    private UUID id;
    private String message;
    private Instant sentAt;
    private UUID chatGroupId; 
    private Instant readAt;
    private String senderID; 

    
}
