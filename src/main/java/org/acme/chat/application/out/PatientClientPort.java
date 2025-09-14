package org.acme.chat.application.out;
import org.acme.shared.PatientChatDto;
import io.smallrye.mutiny.Uni;

public interface PatientClientPort {
      Uni<PatientChatDto> getChatPatient(String userId);

}
