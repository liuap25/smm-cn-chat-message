package org.acme.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PatientProfileChatOut {
    private String userId;
    private String fullName;
    private String photoUrl;  
    
}
