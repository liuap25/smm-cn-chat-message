package org.acme.shared.Search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientSearchResultDTO {
     private String id;
    private String fullName;
    
}
