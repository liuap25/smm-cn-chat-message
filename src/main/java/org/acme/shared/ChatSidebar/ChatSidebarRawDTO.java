package org.acme.shared.ChatSidebar;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatSidebarRawDTO {
    private String chatGroupId;
    private String otherUserId;
    private String lastMessage;
    private String lastMessageDate;
    private int unreadCount;
}