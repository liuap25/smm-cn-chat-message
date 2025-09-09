package org.acme.shared;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PsychologistChatDto {
    private String id;
    private String fullName;
    private String photoUrl;
}
