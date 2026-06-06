package com.edenred.taggy_sustain.service;

import com.edenred.taggy_sustain.dto.CalculoB2CRequest;
import com.edenred.taggy_sustain.dto.CalculoB2CResponse;
import com.edenred.taggy_sustain.model.CalculoImpactoLog;
import com.edenred.taggy_sustain.model.FuelType;
import com.edenred.taggy_sustain.repository.CalculoImpactoLogRepository;
import com.edenred.taggy_sustain.service.strategy.FuelEmissionStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CalculoSimplificadoServiceTest {

    private CalculoSimplificadoService calculoSimplificadoService;

    @Mock
    private CalculoImpactoLogRepository calculoImpactoLogRepository;

    @Mock
    private FuelEmissionStrategy gasolineEmissionStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Map<String, FuelEmissionStrategy> strategyMap = new HashMap<>();
        strategyMap.put("GASOLINE", gasolineEmissionStrategy);
        calculoSimplificadoService = new CalculoSimplificadoService(strategyMap, calculoImpactoLogRepository);
    }

    @Test
    void testCalcularImpactoSimplificadoGasoline() {
        // Arrange
        CalculoB2CRequest request = new CalculoB2CRequest();
        request.setFuelType(FuelType.GASOLINE);
        request.setTotalPassagensPedagio(50);
        request.setTotalPassagensEstacionamento(50);

        // Mocking the strategy
        when(gasolineEmissionStrategy.getEmissionFactor()).thenReturn(new BigDecimal("2.33")); // Emission factor for gasoline

        // Act
        CalculoB2CResponse response = calculoSimplificadoService.calcularImpactoSimplificado(request);

        // Assert Response
        assertNotNull(response);
        assertEquals(4115.00, response.getGramasCo2Evitados(), 0.01);
        assertEquals(1.75, response.getLitrosCombustivelEvitados(), 0.01);
        assertEquals(200.00, response.getGramasPapelEvitados(), 0.01);
        assertEquals(0.19, response.getArvoresEquivalentes(), 0.01);

        // Assert Log Capturing
        ArgumentCaptor<CalculoImpactoLog> logCaptor = ArgumentCaptor.forClass(CalculoImpactoLog.class);
        verify(calculoImpactoLogRepository).save(logCaptor.capture());

        CalculoImpactoLog savedLog = logCaptor.getValue();
        assertNotNull(savedLog);
        assertEquals("calculo_simplificado@taggy.com", savedLog.getEmailUsuario());
        assertEquals(4115.00, savedLog.getGramasCo2Evitados(), 0.01);
        assertEquals(0.19, savedLog.getGramasCo2Evitados(), 0.01);
        assertEquals(9000, savedLog.getTempoGanhoSegundos()); // ( (2*50) + (1*50) ) * 60
        assertNotNull(savedLog.getDataCalculo());
    }
}
