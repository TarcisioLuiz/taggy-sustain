package com.edenred.taggy_sustain.dto;

import com.edenred.taggy_sustain.model.FuelType;
import lombok.Data;

@Data
public class CalculoB2CRequest {
    private String nomeCompleto;
    private String email;
    private String marcaVeiculo;
    private String anoVeiculo;
    private String modeloVeiculo;
    private int totalPassagensPedagio;
    private int totalPassagensEstacionamento;
    private String fuelType;
}
