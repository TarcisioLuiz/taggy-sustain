package com.edenred.taggy_sustain.dto;

import lombok.Data;


@Data
public class CalculoB2CResponse {
    private double gramasCo2Evitados;
    private double gramasPapelEvitados;
    private Double arvoresEquivalentes;
    private long tempoGanhoSegundos;
}
