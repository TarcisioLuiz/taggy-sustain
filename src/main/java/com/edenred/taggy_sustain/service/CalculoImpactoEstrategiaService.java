package com.edenred.taggy_sustain.service;

import com.edenred.taggy_sustain.dto.CalculoImpactoRequestDTO;
import com.edenred.taggy_sustain.dto.ImpactoResponseDTO;
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
public class CalculoImpactoEstrategiaService {

    private static final BigDecimal MINUTES_IN_HOUR = new BigDecimal("60");
    private static final BigDecimal DEFAULT_PAPER_EMISSION_FACTOR = new BigDecimal("1.2"); // Using the value from the old service
    private static final BigDecimal KG_TO_GRAMS = new BigDecimal("1000");
    private static final BigDecimal KG_CO2_PER_TREE_YEAR = new BigDecimal("22");


    private final Map<String, FuelEmissionStrategy> strategyMap;
    private final CalculoImpactoLogRepository repository;

    @Autowired
    public CalculoImpactoEstrategiaService(Map<String, FuelEmissionStrategy> strategyMap, CalculoImpactoLogRepository repository) {
        this.strategyMap = strategyMap;
        this.repository = repository;
    }

    public ImpactoResponseDTO calcularImpacto(CalculoImpactoRequestDTO request) {
        validateRequest(request);

        FuelEmissionStrategy strategy = resolveStrategy(request.getFuelType());
        BigDecimal emissionFactor = strategy.getEmissionFactor();

        // E_marcha_lenta = (t_fila / 60) * C_ml * FE_comb
        BigDecimal timeFraction = request.getTFila().divide(MINUTES_IN_HOUR, 4, RoundingMode.HALF_UP);
        BigDecimal eMarchaLenta = timeFraction
                .multiply(request.getConsumoMarchaLenta())
                .multiply(emissionFactor);

        // E_aceleracao = C_adc * FE_comb
        BigDecimal eAceleracao = request.getConsumoAdicional()
                .multiply(emissionFactor);

        // E_ticket = P_ticket * FE_papel
        BigDecimal paperEmissionFactor = request.getFatorEmissaoPapel() != null
                ? request.getFatorEmissaoPapel()
                : DEFAULT_PAPER_EMISSION_FACTOR;

        BigDecimal eTicket = request.getPesoTicket()
                .multiply(paperEmissionFactor);

        // E_total_evitada = (E_marcha_lenta + E_aceleracao + E_ticket) * N_carros
        BigDecimal totalCo2EvitadoKg = eMarchaLenta.add(eAceleracao).add(eTicket)
                .multiply(request.getVolumeCarros());

        // Conversions for the response DTO
        BigDecimal gramasCo2Evitados = totalCo2EvitadoKg.multiply(KG_TO_GRAMS);
        BigDecimal arvoresEquivalentes = totalCo2EvitadoKg.divide(KG_CO2_PER_TREE_YEAR, 2, RoundingMode.HALF_UP);

        // The percentage reduction calculation is a bit tricky without the "total manual emissions".
        // For simplicity, I'll keep the logic from the old service, adapted for BigDecimal.
        // This is a simplification and might need review for full accuracy.
        BigDecimal consumoManual = request.getTFila().divide(MINUTES_IN_HOUR, 4, RoundingMode.HALF_UP).multiply(request.getConsumoMarchaLenta()).add(request.getConsumoAdicional());
        BigDecimal emissoesTotaisManuais = consumoManual.multiply(emissionFactor).add(eTicket).multiply(request.getVolumeCarros());
        BigDecimal percentualReducao = emissoesTotaisManuais.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
                totalCo2EvitadoKg.divide(emissoesTotaisManuais, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));


        ImpactoResponseDTO response = new ImpactoResponseDTO();
        response.setGramasCo2Evitados(gramasCo2Evitados.setScale(2, RoundingMode.HALF_UP).doubleValue());
        response.setPercentualReducao(percentualReducao.setScale(2, RoundingMode.HALF_UP).doubleValue());
        response.setArvoresEquivalentes(arvoresEquivalentes.setScale(2, RoundingMode.HALF_UP).doubleValue());

        salvarLog(request, response);

        return response;
    }

    private void salvarLog(CalculoImpactoRequestDTO request, ImpactoResponseDTO response) {
        CalculoImpactoLog log = new CalculoImpactoLog();
        log.setTipoVeiculo("N/A"); // New DTO doesn't have this field
        log.setTipoCombustivel(request.getFuelType().name());
        log.setTotalPassagens(request.getVolumeCarros().intValue());
        log.setGramasCo2Evitados(response.getGramasCo2Evitados());
        log.setPercentualReducao(response.getPercentualReducao());
        log.setArvoresEquivalentes(response.getArvoresEquivalentes());
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

    private void validateRequest(CalculoImpactoRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("Requisição não pode ser nula.");
        if (request.getTFila() == null || request.getTFila().compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Tempo de fila inválido ou negativo.");
        if (request.getConsumoMarchaLenta() == null || request.getConsumoMarchaLenta().compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Consumo em marcha lenta inválido ou negativo.");
        if (request.getConsumoAdicional() == null || request.getConsumoAdicional().compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Consumo adicional inválido ou negativo.");
        if (request.getVolumeCarros() == null || request.getVolumeCarros().compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Volume de carros inválido ou negativo.");
        if (request.getPesoTicket() != null && request.getPesoTicket().compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Peso do ticket não pode ser negativo.");
    }
}
