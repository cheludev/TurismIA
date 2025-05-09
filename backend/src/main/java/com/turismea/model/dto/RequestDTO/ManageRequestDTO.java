package com.turismea.model.dto.RequestDTO;



import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.RequestStatus;
import lombok.Data;

@Data
public class ManageRequestDTO {
    private Long id;
    private RequestStatus status;
    private Province province;  // Puede ser null
}