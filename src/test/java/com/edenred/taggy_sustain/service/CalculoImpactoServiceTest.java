package com.edenred.taggy_sustain.service;

import com.edenred.taggy_sustain.dto.ImpactoRequestDTO;
import com.edenred.taggy_sustain.dto.ImpactoResponseDTO;
import com.edenred.taggy_sustain.model.CalculoImpactoLog;
import com.edenred.taggy_sustain.repository.CalculoImpactoLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

class CalculoImpactoServiceTest {

    @InjectMocks
    private CalculoImpactoService calculoImpactoService;

    @Mock
    private CalculoImpactoLogRepository calculoImpactoLogRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalcularImpacto() {
        ImpactoRequestDTO request = new ImpactoRequestDTO();
        request.setTipoVeiculo("leve");
        request.setTipoCombustivel("gasolina");
        request.setTotalPassagens(100);

        ImpactoResponseDTO response = calculoImpactoService.calcularImpacto(request);

        assertNotNull(response);
        assertEquals(638.93, response.getGramasCo2Evitados(), 0.01);
        assertEquals(80.56, response.getPercentualReducao(), 0.01);
        assertEquals(0.03, response.getArvoresEquivalentes(), 0.01);

        ArgumentCaptor<CalculoImpactoLog> logCaptor = ArgumentCaptor.forClass(CalculoImpactoLog.class);
        verify(calculoImpactoLogRepository).save(logCaptor.capture());

        CalculoImpactoLog savedLog = logCaptor.getValue();
        assertEquals("leve", savedLog.getTipoVeiculo());
        assertEquals(100, savedLog.getTotalPassagens());
        assertEquals(638.93, savedLog.getGramasCo2Evitados(), 0.01);
    }
}
