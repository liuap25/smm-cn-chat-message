package org.acme.shared.ChatSidebar;

public class MessageEvent {
    private String chatGroupId;
    private String receiverId;
    private boolean isRead;

    // Constructor, getters, setters
    public MessageEvent(String chatGroupId, String receiverId, boolean isRead) {
        this.chatGroupId = chatGroupId;
        this.receiverId = receiverId;
        this.isRead = isRead;
    }

    public String getChatGroupId() { return chatGroupId; }
    public String getReceiverId() { return receiverId; }
    public boolean isRead() { return isRead; }
    
}
