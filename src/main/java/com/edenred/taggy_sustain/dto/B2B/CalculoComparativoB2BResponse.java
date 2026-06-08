package com.edenred.taggy_sustain.dto.B2B;

import com.edenred.taggy_sustain.dto.GanhosSustentabilidadeDTO;
import com.edenred.taggy_sustain.dto.ResultadoCenarioDTO;
import lombok.Builder;
import lombok.Data;

import java.time.YearMonth;

@Data
@Builder
public class CalculoComparativoB2BResponse {
    private String veiculoInfo;
    private YearMonth mesReferencia;
    private ResultadoCenarioDTO cenarioComTaggy;
    private ResultadoCenarioDTO cenarioSemTaggy;
    private GanhosSustentabilidadeDTO ganhos;
}
