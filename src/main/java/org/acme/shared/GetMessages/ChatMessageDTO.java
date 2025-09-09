package org.acme.shared.GetMessages;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
     private String id;
    private String message;
    private Instant sentAt;
    private String senderId;
    private String receiverId;
    private String senderFullName;
    private String senderPhotoUrl;
    

     public ChatMessageDTO(String id, String message, Instant sentAt,
                          String senderId, String receiverId) {
        this.id = id;
        this.message = message;
        this.sentAt = sentAt;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }


}

    

