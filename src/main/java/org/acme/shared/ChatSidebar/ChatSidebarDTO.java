package org.acme.shared.ChatSidebar;

import java.time.Instant;



import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatSidebarDTO {
    private String chatGroupId;
    private String otherUserId;
    private String fullName;
    private String photoUrl;
    private String lastMessage;
    private Instant lastMessageDate;
    private long unreadCount;
    
}

