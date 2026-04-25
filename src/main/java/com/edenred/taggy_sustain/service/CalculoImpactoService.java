package com.edenred.taggy_sustain.service;

import com.edenred.taggy_sustain.dto.ImpactoRequestDTO;
import com.edenred.taggy_sustain.dto.ImpactoResponseDTO;
import com.edenred.taggy_sustain.model.CalculoImpactoLog;
import com.edenred.taggy_sustain.repository.CalculoImpactoLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class CalculoImpactoService {

    @Autowired
    private CalculoImpactoLogRepository repository;

    private static final double EMISSAO_GASOLINA = 2.27;
    private static final double CONSUMO_IDLE_LEVE = 0.8;
    private static final double ADICIONAL_ACELERACAO = 0.015;
    private static final int TEMPO_FILA_MANUAL = 120;
    private static final int TEMPO_TAG = 5;
    private static final double PESO_TICKET_PAPEL = 0.002;
    private static final double FATOR_EMISSAO_PAPEL = 1.2;

    public ImpactoResponseDTO calcularImpacto(ImpactoRequestDTO request) {
        double consumoManual = ((double) TEMPO_FILA_MANUAL / 3600) * CONSUMO_IDLE_LEVE + ADICIONAL_ACELERACAO;
        double consumoTag = ((double) TEMPO_TAG / 3600) * CONSUMO_IDLE_LEVE;

        double consumoEvitado = (consumoManual - consumoTag) * request.getTotalPassagens();
        double co2EvitadoCombustivel = consumoEvitado * EMISSAO_GASOLINA;
        double co2EvitadoPapel = PESO_TICKET_PAPEL * FATOR_EMISSAO_PAPEL * request.getTotalPassagens();
        double totalCo2EvitadoKg = co2EvitadoCombustivel + co2EvitadoPapel;

        double gramasCo2Evitados = totalCo2EvitadoKg * 1000;

        double emissoesTotaisManuais = (consumoManual * EMISSAO_GASOLINA + PESO_TICKET_PAPEL * FATOR_EMISSAO_PAPEL) * request.getTotalPassagens();
        double percentualReducao = (totalCo2EvitadoKg / emissoesTotaisManuais) * 100;

        double arvoresEquivalentes = totalCo2EvitadoKg / 22;

        ImpactoResponseDTO response = new ImpactoResponseDTO();
        response.setGramasCo2Evitados(arredondar(gramasCo2Evitados, 2));
        response.setPercentualReducao(arredondar(percentualReducao, 2));
        response.setArvoresEquivalentes(arredondar(arvoresEquivalentes, 2));

        salvarLog(request, response);

        return response;
    }

    private void salvarLog(ImpactoRequestDTO request, ImpactoResponseDTO response) {
        CalculoImpactoLog log = new CalculoImpactoLog();
        log.setTipoVeiculo(request.getTipoVeiculo());
        log.setTipoCombustivel(request.getTipoCombustivel());
        log.setTotalPassagens(request.getTotalPassagens());
        log.setGramasCo2Evitados(response.getGramasCo2Evitados());
        log.setPercentualReducao(response.getPercentualReducao());
        log.setArvoresEquivalentes(response.getArvoresEquivalentes());
        log.setDataCalculo(LocalDateTime.now());
        repository.save(log);
    }

    private double arredondar(double valor, int casasDecimais) {
        if (casasDecimais < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(valor));
        bd = bd.setScale(casasDecimais, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
