package com.edenred.taggy_sustain.dto;

import lombok.Data;

@Data
public class CalculoSimplificadoResponseDTO {
    private Double litrosCombustivelEvitados;
    private Double gramasCo2Evitados;
    private Double gramasPapelEvitados;
    private Double arvoresEquivalentes;
}
