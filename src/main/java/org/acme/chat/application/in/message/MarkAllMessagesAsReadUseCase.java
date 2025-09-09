package org.acme.chat.application.in.message;

import java.util.List;

import org.acme.shared.ChatMessage.ChatMessageResponseDto;

import io.smallrye.mutiny.Uni;

public interface MarkAllMessagesAsReadUseCase {
   Uni<List<ChatMessageResponseDto>> markAllAsRead(String chatGroupId, String readerId);

}
