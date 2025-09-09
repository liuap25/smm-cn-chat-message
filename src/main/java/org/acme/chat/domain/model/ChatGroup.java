package org.acme.chat.domain.model;

import java.time.Instant;

public record ChatGroup(
    String id,
    String psychologistId,
    String patientId,
    Instant createdAt,
    Instant updatedAt
) {
} 