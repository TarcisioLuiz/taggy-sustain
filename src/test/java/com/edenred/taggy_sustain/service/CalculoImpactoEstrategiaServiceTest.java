package com.edenred.taggy_sustain.service;

import com.edenred.taggy_sustain.dto.CalculoImpactoRequestDTO;
import com.edenred.taggy_sustain.dto.ImpactoResponseDTO;
import com.edenred.taggy_sustain.model.CalculoImpactoLog;
import com.edenred.taggy_sustain.model.FuelType;
import com.edenred.taggy_sustain.repository.CalculoImpactoLogRepository;
import com.edenred.taggy_sustain.service.strategy.FuelEmissionStrategy;
import com.edenred.taggy_sustain.service.strategy.GasolineEmissionStrategy;
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

class CalculoImpactoEstrategiaServiceTest {

    private CalculoImpactoEstrategiaService calculoImpactoService;

    @Mock
    private CalculoImpactoLogRepository calculoImpactoLogRepository;

    @Mock
    private GasolineEmissionStrategy gasolineEmissionStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Map<String, FuelEmissionStrategy> strategyMap = new HashMap<>();
        strategyMap.put("GASOLINE", gasolineEmissionStrategy);
        calculoImpactoService = new CalculoImpactoEstrategiaService(strategyMap, calculoImpactoLogRepository);
    }

    @Test
    void testCalcularImpactoGasoline() {
        // Given
        CalculoImpactoRequestDTO request = new CalculoImpactoRequestDTO();
        request.setFuelType(FuelType.GASOLINE);
        request.setTFila(new BigDecimal("2")); // 2 minutes
        request.setConsumoMarchaLenta(new BigDecimal("0.8")); // 0.8 L/h
        request.setConsumoAdicional(new BigDecimal("0.015")); // 0.015 L
        request.setPesoTicket(new BigDecimal("0.002")); // 2g
        request.setVolumeCarros(new BigDecimal("100"));

        when(gasolineEmissionStrategy.getEmissionFactor()).thenReturn(new BigDecimal("2.33"));

        // When
        ImpactoResponseDTO response = calculoImpactoService.calcularImpacto(request);

        // Then
        assertNotNull(response);
        assertEquals(945.33, response.getGramasCo2Evitados(), 0.01);
        assertEquals(97.51, response.getPercentualReducao(), 0.01);
        assertEquals(0.04, response.getArvoresEquivalentes(), 0.01);

        ArgumentCaptor<CalculoImpactoLog> logCaptor = ArgumentCaptor.forClass(CalculoImpactoLog.class);
        verify(calculoImpactoLogRepository).save(logCaptor.capture());

        CalculoImpactoLog savedLog = logCaptor.getValue();
        assertEquals("GASOLINE", savedLog.getTipoCombustivel());
        assertEquals(100, savedLog.getTotalPassagens());
        assertEquals(945.33, savedLog.getGramasCo2Evitados(), 0.01);
    }
}
