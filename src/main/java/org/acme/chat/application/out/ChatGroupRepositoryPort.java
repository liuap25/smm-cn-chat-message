package org.acme.chat.application.out;

import java.util.List;

import org.acme.chat.domain.entity.ChatMessageEntity;
import org.acme.chat.domain.model.ChatGroup;



import io.smallrye.mutiny.Uni;

public interface ChatGroupRepositoryPort {
     Uni<ChatGroup> save(ChatGroup chatGroup);  
     Uni<ChatGroup> findById(String chatGroupId);
      
     Uni<List<ChatGroup>> findChatGroupsByUserId(String userId);
     Uni<ChatMessageEntity> findLastMessage(String chatGroupId);


     Uni<List<ChatGroup>> findUnreadChatGroupsByUserId(String userId);
     Uni<Long> countUnreadMessages(String chatGroupId, String userId);


 


     
    
}
