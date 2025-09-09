package org.acme.shared.GetMessages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserDTO {
    private String id;
    private String fullName;
    private String photoUrl;
    
}
