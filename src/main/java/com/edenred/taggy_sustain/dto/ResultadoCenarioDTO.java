package com.edenred.taggy_sustain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResultadoCenarioDTO {
    private double gramasCo2Emitidos;
    private double litrosCombustivelConsumidos;
    private double gramasPapelUtilizados;
    private long tempoEstimadoUtilizado;
}
