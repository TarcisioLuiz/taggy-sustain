package com.edenred.taggy_sustain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResultadoCenarioDTO {
    private double gramasCo2Emitidos;
    private double litrosCombustivelConsumidos;
    private double gramasPapelUtilizados;
    private BigDecimal tempoEstimadoUtilizado;
    private Double arvoresEquivalentes;
}
