package com.edenred.taggy_sustain.service;

import com.edenred.taggy_sustain.dto.B2B.CalculoComparativoB2BResponse;
import com.edenred.taggy_sustain.dto.GanhosSustentabilidadeDTO;
import com.edenred.taggy_sustain.dto.ResultadoCenarioDTO;
import com.edenred.taggy_sustain.model.*;
import com.edenred.taggy_sustain.repository.DadosCalculoRepository;
import com.edenred.taggy_sustain.repository.UsuarioRepository;
import com.edenred.taggy_sustain.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalculoB2BService {

    private final VeiculoRepository veiculoRepository;
    private final DadosCalculoRepository dadosCalculoRepository;
    private final UsuarioRepository pessoaRepository;
    private final CalculoSimplificadoService calculoSimplificadoService;

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
                    FuelType fuelType = FuelType.valueOf(veiculo.getFuelType().toUpperCase());
                    ResultadoCenarioDTO cenarioSemTaggy = calculoSimplificadoService.calcularImpactoSimplificado(fuelType, dadosCalculo.getQtdPassagensPedagio(), dadosCalculo.getQtdPassagensEstacionamento(), false);
                    ResultadoCenarioDTO cenarioComTaggy = calculoSimplificadoService.calcularImpactoSimplificado(fuelType, dadosCalculo.getQtdPassagensPedagio(), dadosCalculo.getQtdPassagensEstacionamento(), true);

                    GanhosSustentabilidadeDTO ganhos = calculoSimplificadoService.calcularGanhos(cenarioComTaggy, cenarioSemTaggy);

                    calculoSimplificadoService.salvarLog(pessoa.getEmail(), ganhos);

                    CalculoComparativoB2BResponse comparativoDTO = CalculoComparativoB2BResponse.builder()
                        .veiculoInfo(veiculo.getModelo())
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
}
