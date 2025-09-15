package org.acme.chat.domain.model;

import java.time.Instant;

public record ChatMessage(
        String id,
        String chatGroupId,
        String senderId,
        String receiverId,
        String message,
        Instant sentAt,
        Instant readAt
) {}

