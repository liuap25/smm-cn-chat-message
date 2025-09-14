package org.acme.chat.application.in.message;

import io.smallrye.mutiny.Uni;

public interface MarkAllAsReadPsychologistUseCase {

    Uni<Boolean> markAllAsReadPsychologist(String chatGroupId, String readerId);
  
}
