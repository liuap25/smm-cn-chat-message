package org.acme.chat.domain.model;

import java.time.Instant;

public record ChatMessageUpdate(
    String id,
    Instant readAt

){

}
