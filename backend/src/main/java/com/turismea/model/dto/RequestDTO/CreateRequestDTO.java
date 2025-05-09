package com.turismea.model.dto.RequestDTO;

import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.RequestType;

public class CreateRequestDTO {
    public Long userId;
    public RequestType type;
    public String reasons;
    public Province province;
}

