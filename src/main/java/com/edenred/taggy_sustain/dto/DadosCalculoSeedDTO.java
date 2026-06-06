package com.edenred.taggy_sustain.dto;

import lombok.Data;
import java.time.YearMonth;

@Data
public class DadosCalculoSeedDTO {
    private double kmRodados;
    private String tipoCombustivel;
    private double consumoMedio;
    private YearMonth mesReferencia;
}
