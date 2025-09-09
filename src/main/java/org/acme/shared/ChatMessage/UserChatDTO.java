package org.acme.shared.ChatMessage;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserChatDTO {
    private UUID id;
    private String fullName;
    private String photoUrl;
    
}
