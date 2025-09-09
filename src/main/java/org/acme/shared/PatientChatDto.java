package org.acme.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientChatDto {
    private String id;
    private String fullname;
    private String photoUrl;
    
}
