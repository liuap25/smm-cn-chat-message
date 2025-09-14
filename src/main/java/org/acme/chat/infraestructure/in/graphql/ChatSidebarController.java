package org.acme.chat.infraestructure.in.graphql;
import java.util.List;
import org.acme.chat.application.in.chatsidebar.GetChatSidebarDataUseCase;
import org.acme.chat.application.in.chatsidebar.GetChatSidebarUseCase;
import org.acme.shared.ChatSidebar.ChatSidebarDTO;
import org.acme.shared.ChatSidebar.ChatSidebarRawDTO;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@GraphQLApi
@ApplicationScoped
public class ChatSidebarController {


    @Inject
    GetChatSidebarUseCase getChatSidebarUseCase;

    @Inject
    GetChatSidebarDataUseCase chatSidebarUseCase;


    @Query("getChatSidebarDataExample")
    @Description("Obtiene todos los chats con el último mensaje, fecha y cantidad de no leídos para un usuario")
    public Uni<List<ChatSidebarRawDTO>> getChatSidebarDataExample(String userId) {
        return getChatSidebarUseCase.getChatSidebar(userId);
    }


    @Query("getChatSidebarData")
    public Uni<List<ChatSidebarDTO>> getChatSidebarData(String userId) {
        return chatSidebarUseCase.findChatSidebarData(userId);
    }

}
