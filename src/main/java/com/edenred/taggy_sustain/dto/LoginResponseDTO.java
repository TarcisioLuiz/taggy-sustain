package com.edenred.taggy_sustain.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String mensagem;

    public LoginResponseDTO(String mensagem){
        this.mensagem = mensagem;
    }

}
