package com.edenred.taggy_sustain.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String userId;

    public LoginResponseDTO(String id){
        this.userId = id;
    }

}
