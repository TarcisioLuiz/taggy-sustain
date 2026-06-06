package com.edenred.taggy_sustain.dto;

import lombok.Data;
import java.util.List;

@Data
public class VeiculoSeedDTO {
    private String placa;
    private String modelo;
    private int ano;
    private List<DadosCalculoSeedDTO> dadosCalculo;
}
