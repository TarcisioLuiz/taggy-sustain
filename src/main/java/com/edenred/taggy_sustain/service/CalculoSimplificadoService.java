package com.edenred.taggy_sustain.service;

import com.edenred.taggy_sustain.dto.CalculoB2CRequest;
import com.edenred.taggy_sustain.dto.CalculoB2CResponse;
import com.edenred.taggy_sustain.model.CalculoImpactoLog;
import com.edenred.taggy_sustain.model.FuelType;
import com.edenred.taggy_sustain.repository.CalculoImpactoLogRepository;
import com.edenred.taggy_sustain.service.strategy.FuelEmissionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class CalculoSimplificadoService {

    private static final BigDecimal TEMPO_FILA_PEDAGIO = new BigDecimal("2");
    private static final BigDecimal TEMPO_FILA_ESTACIONAMENTO = new BigDecimal("1");
    private static final BigDecimal CONSUMO_MARCHA_LENTA = new BigDecimal("0.8");
    private static final BigDecimal CONSUMO_ADICIONAL_ACELERACAO = new BigDecimal("0.015");
    private static final BigDecimal PESO_TICKET = new BigDecimal("0.002");
    private static final BigDecimal FATOR_EMISSAO_PAPEL = new BigDecimal("1.2");
    private static final BigDecimal MINUTES_IN_HOUR = new BigDecimal("60");
    private static final BigDecimal KG_TO_GRAMS = new BigDecimal("1000");
    private static final BigDecimal KG_CO2_PER_TREE_YEAR = new BigDecimal("22");

    private final Map<String, FuelEmissionStrategy> strategyMap;
    private final CalculoImpactoLogRepository repository;

    @Autowired
    public CalculoSimplificadoService(Map<String, FuelEmissionStrategy> strategyMap, CalculoImpactoLogRepository repository) {
        this.strategyMap = strategyMap;
        this.repository = repository;
    }

    public CalculoB2CResponse calcularImpactoSimplificado(CalculoB2CRequest request) {
        FuelEmissionStrategy strategy = resolveStrategy(request.getFuelType());
        BigDecimal emissionFactor = strategy.getEmissionFactor();

        BigDecimal tempoTotalFila = TEMPO_FILA_PEDAGIO.multiply(BigDecimal.valueOf(request.getTotalPassagensPedagio()))
                .add(TEMPO_FILA_ESTACIONAMENTO.multiply(BigDecimal.valueOf(request.getTotalPassagensEstacionamento())));

        BigDecimal consumoTotalMarchaLenta = tempoTotalFila.divide(MINUTES_IN_HOUR, 4, RoundingMode.HALF_UP)
                .multiply(CONSUMO_MARCHA_LENTA);

        BigDecimal consumoTotalAceleracao = CONSUMO_ADICIONAL_ACELERACAO
                .multiply(BigDecimal.valueOf(request.getTotalPassagensPedagio() + request.getTotalPassagensEstacionamento()));

        BigDecimal litrosCombustivelEvitados = consumoTotalMarchaLenta.add(consumoTotalAceleracao);

        BigDecimal co2EvitadoCombustivel = litrosCombustivelEvitados.multiply(emissionFactor);

        BigDecimal totalTickets = BigDecimal.valueOf(request.getTotalPassagensPedagio() + request.getTotalPassagensEstacionamento());
        BigDecimal pesoTotalPapel = PESO_TICKET.multiply(totalTickets);
        BigDecimal co2EvitadoPapel = pesoTotalPapel.multiply(FATOR_EMISSAO_PAPEL);

        BigDecimal totalCo2EvitadoKg = co2EvitadoCombustivel.add(co2EvitadoPapel);

        CalculoB2CResponse response = new CalculoB2CResponse();
        response.setLitrosCombustivelEvitados(litrosCombustivelEvitados.setScale(2, RoundingMode.HALF_UP).doubleValue());
        response.setGramasCo2Evitados(totalCo2EvitadoKg.multiply(KG_TO_GRAMS).setScale(2, RoundingMode.HALF_UP).doubleValue());
        response.setGramasPapelEvitados(pesoTotalPapel.multiply(KG_TO_GRAMS).setScale(2, RoundingMode.HALF_UP).doubleValue());
        response.setArvoresEquivalentes(totalCo2EvitadoKg.divide(KG_CO2_PER_TREE_YEAR, 2, RoundingMode.HALF_UP).doubleValue());

        salvarLog(request, response, tempoTotalFila);

        return response;
    }

    private void salvarLog(CalculoB2CRequest request, CalculoB2CResponse response, BigDecimal tempoTotalFila) {
        CalculoImpactoLog log = new CalculoImpactoLog();
        log.setEmailUsuario("calculo_simplificado@taggy.com"); //TODO: colocar email de usuário
        log.setGramasCo2Evitados(response.getGramasCo2Evitados());
        log.setGramasPapelEvitados(response.getGramasPapelEvitados());
        log.setLitrosCombustivelEvitados(response.getLitrosCombustivelEvitados());
        log.setDataCalculo(LocalDateTime.now());
        repository.save(log);
    }

    private FuelEmissionStrategy resolveStrategy(FuelType fuelType) {
        if (fuelType == null) {
            throw new IllegalArgumentException("O tipo de combustível é obrigatório.");
        }
        FuelEmissionStrategy strategy = strategyMap.get(fuelType.name());
        if (strategy == null) {
            throw new IllegalArgumentException("Estratégia de emissão não encontrada para o combustível: " + fuelType.name());
        }
        return strategy;
    }
}
