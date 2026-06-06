package com.edenred.taggy_sustain.dto;

import com.edenred.taggy_sustain.model.FuelType;
import lombok.Data;

@Data
public class CalculoB2CRequest {
    private int totalPassagensPedagio;
    private int totalPassagensEstacionamento;
    private FuelType fuelType;
}
