package com.edenred.taggy_sustain.service;

import com.edenred.taggy_sustain.dto.*;
import com.edenred.taggy_sustain.model.CalculoImpactoLog;
import com.edenred.taggy_sustain.model.FuelType;
import com.edenred.taggy_sustain.model.Pessoa;
import com.edenred.taggy_sustain.model.Veiculo;
import com.edenred.taggy_sustain.repository.CalculoImpactoLogRepository;
import com.edenred.taggy_sustain.repository.UsuarioRepository;
import com.edenred.taggy_sustain.repository.VeiculoRepository;
import com.edenred.taggy_sustain.service.strategy.FuelEmissionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class CalculoSimplificadoService {

    private static final BigDecimal TEMPO_FILA_PEDAGIO_SEM_TAGGY = new BigDecimal("20");
    private static final BigDecimal TEMPO_FILA_PEDAGIO_COM_TAGGY = new BigDecimal("0");
    private static final BigDecimal TEMPO_FILA_ESTACIONAMENTO_SEM_TAGGY = new BigDecimal("10");
    private static final BigDecimal TEMPO_FILA_ESTACIONAMENTO_COM_TAGGY = new BigDecimal("0");
    private static final BigDecimal CONSUMO_MARCHA_LENTA = new BigDecimal("2.0");
    private static final BigDecimal CONSUMO_ADICIONAL_ACELERACAO = new BigDecimal("0.015");
    private static final BigDecimal PESO_TICKET = new BigDecimal("0.002");
    private static final BigDecimal FATOR_EMISSAO_PAPEL = new BigDecimal("1.05");
    private static final BigDecimal MINUTES_IN_HOUR = new BigDecimal("60");
    private static final BigDecimal KG_TO_GRAMS = new BigDecimal("1000");
    private static final BigDecimal KG_CO2_PER_TREE_YEAR = new BigDecimal("22");

    private final Map<String, FuelEmissionStrategy> strategyMap;
    private final CalculoImpactoLogRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final VeiculoRepository veiculoRepository;

    @Autowired
    public CalculoSimplificadoService(Map<String, FuelEmissionStrategy> strategyMap, CalculoImpactoLogRepository repository, UsuarioRepository usuarioRepository, VeiculoRepository veiculoRepository) {
        this.strategyMap = strategyMap;
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.veiculoRepository = veiculoRepository;
    }

    public ResultadoCenarioDTO calcularImpactoSimplificado(FuelType fuelType, int totalPassagensPedagio, int totalPassagensEstacionamento, boolean isTaggy) {
        FuelEmissionStrategy strategy = resolveStrategy(fuelType);
        BigDecimal emissionFactor = strategy.getEmissionFactor();

        BigDecimal tempoFilaPedagio;
        BigDecimal tempoFilaEstacionamento;

        if (isTaggy) {
            tempoFilaPedagio = TEMPO_FILA_PEDAGIO_COM_TAGGY;
            tempoFilaEstacionamento = TEMPO_FILA_ESTACIONAMENTO_COM_TAGGY;
        } else {
            tempoFilaPedagio = TEMPO_FILA_PEDAGIO_SEM_TAGGY;
            tempoFilaEstacionamento = TEMPO_FILA_ESTACIONAMENTO_SEM_TAGGY;
        }

        BigDecimal tempoTotalFila = tempoFilaPedagio.multiply(BigDecimal.valueOf(totalPassagensPedagio))
                .add(tempoFilaEstacionamento.multiply(BigDecimal.valueOf(totalPassagensEstacionamento)));

        BigDecimal consumoTotalMarchaLenta = tempoTotalFila.divide(MINUTES_IN_HOUR, 4, RoundingMode.HALF_UP)
                .multiply(CONSUMO_MARCHA_LENTA);

        BigDecimal consumoTotalAceleracao = CONSUMO_ADICIONAL_ACELERACAO
                .multiply(BigDecimal.valueOf(totalPassagensPedagio + totalPassagensEstacionamento));

        BigDecimal litrosCombustivelConsumidos = consumoTotalMarchaLenta.add(consumoTotalAceleracao);

        BigDecimal co2Combustivel = litrosCombustivelConsumidos.multiply(emissionFactor);

        BigDecimal totalTickets = BigDecimal.valueOf(totalPassagensPedagio + totalPassagensEstacionamento);
        BigDecimal pesoTotalPapel = PESO_TICKET.multiply(totalTickets);
        BigDecimal co2Papel = pesoTotalPapel.multiply(FATOR_EMISSAO_PAPEL);

        BigDecimal totalCo2EvitadoKg = co2Combustivel.add(co2Papel);

        ResultadoCenarioDTO response = new ResultadoCenarioDTO();
        response.setLitrosCombustivelConsumidos(litrosCombustivelConsumidos.setScale(2, RoundingMode.HALF_UP).doubleValue());
        response.setGramasCo2Emitidos(totalCo2EvitadoKg.multiply(KG_TO_GRAMS).setScale(2, RoundingMode.HALF_UP).doubleValue());
        response.setGramasPapelUtilizados(pesoTotalPapel.multiply(KG_TO_GRAMS).setScale(2, RoundingMode.HALF_UP).doubleValue());
        response.setArvoresEquivalentes(totalCo2EvitadoKg.divide(KG_CO2_PER_TREE_YEAR, 2, RoundingMode.HALF_UP).doubleValue());
        response.setTempoEstimadoUtilizado(tempoTotalFila);

        return response;
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

    public CalculoB2CResponse calcularEmissoesMensaisB2C(CalculoB2CRequest request) {
        CalculoB2CResponse resultado = new CalculoB2CResponse();

        Pessoa pessoa = usuarioRepository.findByEmail(request.getEmail())
                .orElseGet(() -> {
                    Pessoa novaPessoa = new Pessoa();
                    novaPessoa.setNome(request.getNomeCompleto());
                    novaPessoa.setEmail(request.getEmail());
                    return usuarioRepository.save(novaPessoa);
                });

        veiculoRepository.findByProprietarioAndModelo(pessoa, request.getModeloVeiculo())
                .orElseGet(() -> {
                    Veiculo novoVeiculo = new Veiculo();
                    novoVeiculo.setProprietario(pessoa);
                    novoVeiculo.setMarca(request.getMarcaVeiculo());
                    novoVeiculo.setModelo(request.getModeloVeiculo());
                    novoVeiculo.setAno(Integer.parseInt(request.getAnoVeiculo()));
                    novoVeiculo.setFuelType(request.getFuelType());
                    return veiculoRepository.save(novoVeiculo);
                });

        FuelType fuelType = FuelType.valueOf(request.getFuelType().toUpperCase());
        ResultadoCenarioDTO cenarioSemTaggy = calcularImpactoSimplificado(fuelType, request.getTotalPassagensPedagio(), request.getTotalPassagensEstacionamento(), false);
        ResultadoCenarioDTO cenarioComTaggy = calcularImpactoSimplificado(fuelType, request.getTotalPassagensPedagio(), request.getTotalPassagensEstacionamento(), true);

        GanhosSustentabilidadeDTO ganhos = calcularGanhos(cenarioComTaggy, cenarioSemTaggy);

        salvarLog(request.getEmail(), ganhos);

        resultado.setArvoresEquivalentes(ganhos.getArvoresEquivalentes());
        resultado.setTempoGanhoSegundos(ganhos.getTempoGanhoSegundos());
        resultado.setGramasCo2Evitados(ganhos.getGramasCo2Evitados());
        resultado.setGramasPapelEvitados(ganhos.getGramasPapelEvitados());


        return resultado;
    }

    public GanhosSustentabilidadeDTO calcularGanhos(ResultadoCenarioDTO cenarioComTaggy, ResultadoCenarioDTO cenarioSemTaggy) {
        double co2Evitado = cenarioSemTaggy.getGramasCo2Emitidos() - cenarioComTaggy.getGramasCo2Emitidos();
        double combustivelEvitado = cenarioSemTaggy.getLitrosCombustivelConsumidos() - cenarioComTaggy.getLitrosCombustivelConsumidos();
        long tempoEvitado = cenarioComTaggy.getTempoEstimadoUtilizado().longValue() - cenarioSemTaggy.getTempoEstimadoUtilizado().longValue();

        return GanhosSustentabilidadeDTO.builder()
                .gramasCo2Evitados(co2Evitado > 0 ? new BigDecimal(co2Evitado).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0)
                .litrosCombustivelEvitados(combustivelEvitado > 0 ? combustivelEvitado : 0)
                .gramasPapelEvitados(cenarioComTaggy.getGramasPapelUtilizados())
                .tempoGanhoSegundos(tempoEvitado > 0 ? tempoEvitado : 0)
                .arvoresEquivalentes(cenarioComTaggy.getArvoresEquivalentes())
                .build();
    }

    public void salvarLog(String emailUsuario, GanhosSustentabilidadeDTO ganhosSustentabilidadeDTO) {
        CalculoImpactoLog log = new CalculoImpactoLog();
        log.setEmailUsuario(emailUsuario);
        log.setGramasCo2Evitados(ganhosSustentabilidadeDTO.getGramasCo2Evitados());
        log.setGramasPapelEvitados(ganhosSustentabilidadeDTO.getGramasPapelEvitados());
        log.setTempoGanhoSegundos(ganhosSustentabilidadeDTO.getTempoGanhoSegundos());
        log.setLitrosCombustivelEvitados(ganhosSustentabilidadeDTO.getLitrosCombustivelEvitados());
        log.setDataCalculo(LocalDateTime.now());
        repository.save(log);
    }
}
