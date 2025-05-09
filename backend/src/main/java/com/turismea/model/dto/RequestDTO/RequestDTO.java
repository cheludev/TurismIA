package com.turismea.model.dto.RequestDTO;


import com.turismea.model.entity.Request;
import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.RequestStatus;
import com.turismea.model.enumerations.RequestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDTO {
    private Long id;
    private Long userId;
    private String username;
    private String reasons;
    private RequestType type;
    private Province province;
    private RequestStatus status;

    public RequestDTO(Request request) {
        this.id = request.getId();
        this.userId = request.getUser().getId();
        this.username = request.getUser().getUsername();
        this.reasons = request.getReasonsOfTheRequest();
        this.type = request.getType();
        this.province = request.getProvince();
        this.status = request.getRequestStatus();
    }
}