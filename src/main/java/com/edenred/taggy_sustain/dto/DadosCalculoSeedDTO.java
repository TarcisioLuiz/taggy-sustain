package com.edenred.taggy_sustain.dto;

import lombok.Data;
import java.time.YearMonth;

@Data
public class DadosCalculoSeedDTO {
    private YearMonth mesReferencia;
    private Integer qtdPassagensEstacionamento;
    private Integer qtdPassagensPedagio;
}
