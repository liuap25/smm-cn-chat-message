package org.acme.chat.application.in.message;
import io.smallrye.mutiny.Uni;

public interface MarkAllMessagesAsReadUseCase {
   Uni<Boolean> markAllAsRead(String chatGroupId, String readerId);

}
