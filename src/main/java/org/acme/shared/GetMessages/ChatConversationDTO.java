package org.acme.shared.GetMessages;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversationDTO {
    private ChatUserDTO otherUser;
    private List<ChatMessageDTO> messages;
    
}
