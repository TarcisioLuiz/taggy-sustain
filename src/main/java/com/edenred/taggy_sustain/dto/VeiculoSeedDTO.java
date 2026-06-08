package com.edenred.taggy_sustain.dto;

import lombok.Data;
import java.util.List;

@Data
public class VeiculoSeedDTO {
    private String marca;
    private String modelo;
    private String fuelType;
    private int ano;
    private List<DadosCalculoSeedDTO> dadosCalculo;
}
