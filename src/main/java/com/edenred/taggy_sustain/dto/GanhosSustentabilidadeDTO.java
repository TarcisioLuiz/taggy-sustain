package com.edenred.taggy_sustain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GanhosSustentabilidadeDTO {
    private double gramasCo2Evitados;
    private double litrosCombustivelEvitados;
    private double gramasPapelEvitados;
    private long tempoGanhoSegundos;
}
