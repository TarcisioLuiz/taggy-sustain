package com.edenred.taggy_sustain.service;

import com.edenred.taggy_sustain.dto.CalculoComparativoB2BResponse;
import com.edenred.taggy_sustain.dto.GanhosSustentabilidadeDTO;
import com.edenred.taggy_sustain.dto.ResultadoCenarioDTO;
import com.edenred.taggy_sustain.model.CalculoImpactoLog;
import com.edenred.taggy_sustain.model.DadosCalculo;
import com.edenred.taggy_sustain.model.Pessoa;
import com.edenred.taggy_sustain.model.Veiculo;
import com.edenred.taggy_sustain.repository.CalculoImpactoLogRepository;
import com.edenred.taggy_sustain.repository.DadosCalculoRepository;
import com.edenred.taggy_sustain.repository.UsuarioRepository;
import com.edenred.taggy_sustain.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalculoB2BService {

    private final VeiculoRepository veiculoRepository;
    private final DadosCalculoRepository dadosCalculoRepository;
    private final CalculoImpactoLogRepository calculoImpactoLogRepository;
    private final UsuarioRepository pessoaRepository;

    private static final double EMISSAO_CO2_POR_LITRO_GASOLINA = 2300; // em gramas
    private static final double CONSUMO_EXTRA_FILA_POR_MINUTO = 0.02; // em litros
    private static final int TEMPO_MEDIO_FILA_SEGUNDOS = 120;
    private static final double GRAMAS_PAPEL_POR_TICKET = 1.5;


    public List<CalculoComparativoB2BResponse> calcularEmissoesMensaisB2B(Long usuarioId, YearMonth mes) {
        Optional<Pessoa> pessoaOpt = pessoaRepository.findById(usuarioId);
        if (pessoaOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado com o ID: " + usuarioId);
        }
        Pessoa pessoa = pessoaOpt.get();

        List<Veiculo> veiculos = veiculoRepository.findByProprietarioId(usuarioId);
        List<CalculoComparativoB2BResponse> resultados = new ArrayList<>();

        for (Veiculo veiculo : veiculos) {
            dadosCalculoRepository.findByVeiculoAndMesReferencia(veiculo, mes)
                .ifPresent(dadosCalculo -> {
                    ResultadoCenarioDTO cenarioComTaggy = calcularCenarioComTaggy(dadosCalculo);
                    ResultadoCenarioDTO cenarioSemTaggy = calcularCenarioSemTaggy(cenarioComTaggy);

                    GanhosSustentabilidadeDTO ganhos = calcularGanhos(cenarioComTaggy, cenarioSemTaggy);

                    salvarLog(pessoa.getEmail(), ganhos);

                    CalculoComparativoB2BResponse comparativoDTO = CalculoComparativoB2BResponse.builder()
                        .veiculoInfo(veiculo.getModelo() + " - " + veiculo.getPlaca())
                        .mesReferencia(mes)
                        .cenarioComTaggy(cenarioComTaggy)
                        .cenarioSemTaggy(cenarioSemTaggy)
                        .ganhos(ganhos)
                        .build();

                    resultados.add(comparativoDTO);
                });
        }

        return resultados;
    }

    private ResultadoCenarioDTO calcularCenarioComTaggy(DadosCalculo dados) {
        double litrosConsumidos = dados.getKmRodados() / dados.getConsumoMedio();
        double co2Emitido = litrosConsumidos * EMISSAO_CO2_POR_LITRO_GASOLINA;
        //TODO: Adicionar quantidade de passagens por pedagio/estacionamento
        //TODO: Adicionar tempo médio de tempo em fila de espera
        // Assumindo que o tempo ganho é uma estimativa (ex: 2 min por dia útil no mês)
        long tempoGanho = 2 * 60 * 22; // 2 minutos * 22 dias úteis

        return ResultadoCenarioDTO.builder()
            .gramasCo2Emitidos(co2Emitido)
            .litrosCombustivelConsumidos(litrosConsumidos)
            .gramasPapelUtilizados(0)
            .tempoEstimadoUtilizado(tempoGanho)
            .build();
    }

    private ResultadoCenarioDTO calcularCenarioSemTaggy(ResultadoCenarioDTO cenarioComTaggy) {
        // Estimativa de passagens em pedágio/estacionamento (ex: 2 por dia útil)
        int numPassagens = 2 * 22;

        //TODO: Adicionar quantidade de passagens por pedagio/estacionamento
        //TODO: Adicionar tempo médio de tempo em fila de espera
        double litrosExtras = CONSUMO_EXTRA_FILA_POR_MINUTO * (TEMPO_MEDIO_FILA_SEGUNDOS / 60.0) * numPassagens;
        double co2Extra = litrosExtras * EMISSAO_CO2_POR_LITRO_GASOLINA;
        double papelUtilizado = GRAMAS_PAPEL_POR_TICKET * numPassagens;

        return ResultadoCenarioDTO.builder()
            .gramasCo2Emitidos(cenarioComTaggy.getGramasCo2Emitidos() + co2Extra)
            .litrosCombustivelConsumidos(cenarioComTaggy.getLitrosCombustivelConsumidos() + litrosExtras)
            .gramasPapelUtilizados(papelUtilizado)
            .tempoEstimadoUtilizado(0) //TODO: ajustar tempo utilizado
            .build();
    }

    private GanhosSustentabilidadeDTO calcularGanhos(ResultadoCenarioDTO cenarioComTaggy, ResultadoCenarioDTO cenarioSemTaggy) {
        double co2Evitado = cenarioSemTaggy.getGramasCo2Emitidos() - cenarioComTaggy.getGramasCo2Emitidos();
        double combustivelEvitado = cenarioSemTaggy.getLitrosCombustivelConsumidos() - cenarioComTaggy.getLitrosCombustivelConsumidos();
        
        return GanhosSustentabilidadeDTO.builder()
            .gramasCo2Evitados(co2Evitado > 0 ? co2Evitado : 0)
            .litrosCombustivelEvitados(combustivelEvitado > 0 ? combustivelEvitado : 0)
            .gramasPapelEvitados(cenarioSemTaggy.getGramasPapelUtilizados())
            .tempoGanhoSegundos(cenarioComTaggy.getTempoEstimadoUtilizado())
            .build();
    }

    private void salvarLog(String emailUsuario, GanhosSustentabilidadeDTO ganhosSustentabilidadeDTO) {
        CalculoImpactoLog log = new CalculoImpactoLog();
        log.setEmailUsuario(emailUsuario);
        log.setGramasCo2Evitados(ganhosSustentabilidadeDTO.getGramasCo2Evitados());
        log.setGramasPapelEvitados(ganhosSustentabilidadeDTO.getGramasPapelEvitados());
        log.setTempoGanhoSegundos(ganhosSustentabilidadeDTO.getTempoGanhoSegundos());
        log.setLitrosCombustivelEvitados(ganhosSustentabilidadeDTO.getLitrosCombustivelEvitados());
        log.setDataCalculo(LocalDateTime.now());
        calculoImpactoLogRepository.save(log);
    }
}
