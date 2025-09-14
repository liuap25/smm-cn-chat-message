package org.acme.chat.infraestructure.out.event;

import java.time.Instant;

import org.acme.shared.ChatSidebar.ChatSidebarDTO;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ChatSidebarSubscriber {

     // --------- Psychologist ---------
    private final BroadcastProcessor<ChatSidebarDTO> psychologistAllProcessor = BroadcastProcessor.create();
    private final BroadcastProcessor<ChatSidebarDTO> psychologistUnreadProcessor = BroadcastProcessor.create();

    // --------- Patient ---------
    private final BroadcastProcessor<ChatSidebarDTO> patientAllProcessor = BroadcastProcessor.create();
    private final BroadcastProcessor<ChatSidebarDTO> patientUnreadProcessor = BroadcastProcessor.create();

    // ------- Incoming Consumers --------
    @Incoming("sidebar-psychologist-all-in")
    public void consumePsychologistAll(JsonObject json) {
        ChatSidebarDTO dto = mapJsonToDto(json);
        System.out.println("📩 Sidebar ALL Psychologist recibido: " + dto);
        psychologistAllProcessor.onNext(dto);
    }

    @Incoming("sidebar-psychologist-unread-in")
    public void consumePsychologistUnread(JsonObject json) {
        ChatSidebarDTO dto = mapJsonToDto(json);
        System.out.println("📩 Sidebar UNREAD Psychologist recibido: " + dto);
        psychologistUnreadProcessor.onNext(dto);
    }

    @Incoming("sidebar-patient-all-in")
    public void consumePatientAll(JsonObject json) {
        ChatSidebarDTO dto = mapJsonToDto(json);
        System.out.println("📩 Sidebar ALL Patient recibido: " + dto);
        patientAllProcessor.onNext(dto);
    }

    @Incoming("sidebar-patient-unread-in")
    public void consumePatientUnread(JsonObject json) {
        ChatSidebarDTO dto = mapJsonToDto(json);
        System.out.println("📩 Sidebar UNREAD Patient recibido: " + dto);
        patientUnreadProcessor.onNext(dto);
    }

    // ------- Multi Exposed para GraphQL Subscriptions --------
    public Multi<ChatSidebarDTO> getPsychologistAllSidebarUpdates() {
        return psychologistAllProcessor;
    }

    public Multi<ChatSidebarDTO> getPsychologistUnreadSidebarUpdates() {
        return psychologistUnreadProcessor;
    }

    public Multi<ChatSidebarDTO> getPatientAllSidebarUpdates() {
        return patientAllProcessor;
    }

    public Multi<ChatSidebarDTO> getPatientUnreadSidebarUpdates() {
        return patientUnreadProcessor;
    }

    // ------- Mapper --------
    private ChatSidebarDTO mapJsonToDto(JsonObject json) {
        return new ChatSidebarDTO(
            json.getString("chatGroupId"),
            json.getString("otherUserId"),
            json.getString("fullName"),
            json.getString("photoUrl"),
            json.getString("lastMessage"),
            json.containsKey("lastMessageDate") && json.getString("lastMessageDate") != null
                ? Instant.parse(json.getString("lastMessageDate"))
                : null,
            json.containsKey("unreadCount") && json.getValue("unreadCount") != null
                ? json.getLong("unreadCount")
                : 0L
        );
    }

    
}
