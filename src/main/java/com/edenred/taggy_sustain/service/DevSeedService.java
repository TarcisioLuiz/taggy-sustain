package com.edenred.taggy_sustain.service;

import com.edenred.taggy_sustain.dto.DadosCalculoSeedDTO;
import com.edenred.taggy_sustain.dto.DevSeedRequestDTO;
import com.edenred.taggy_sustain.dto.VeiculoSeedDTO;
import com.edenred.taggy_sustain.model.DadosCalculo;
import com.edenred.taggy_sustain.model.Pessoa;
import com.edenred.taggy_sustain.model.Veiculo;
import com.edenred.taggy_sustain.repository.DadosCalculoRepository;
import com.edenred.taggy_sustain.repository.UsuarioRepository;
import com.edenred.taggy_sustain.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DevSeedService {

    private final VeiculoRepository veiculoRepository;
    private final DadosCalculoRepository dadosCalculoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void seedDatabase(DevSeedRequestDTO request) {
        Pessoa pessoa = upsertPessoa(request);

        Optional.ofNullable(request.getVeiculos()).orElse(Collections.emptyList()).forEach(veiculoDTO -> {
            Veiculo veiculo = upsertVeiculo(veiculoDTO, pessoa);
            Optional.ofNullable(veiculoDTO.getDadosCalculo()).orElse(Collections.emptyList()).forEach(dadosCalculoDTO -> {
                upsertDadosCalculo(dadosCalculoDTO, veiculo);
            });
        });
    }

    private Pessoa upsertPessoa(DevSeedRequestDTO request) {
        return usuarioRepository.findByEmail(request.getEmail()).map(existingPessoa -> {
            existingPessoa.setNome(request.getNome());
            existingPessoa.setIdade(request.getIdade());
            existingPessoa.setCpfCnpj(request.getCpfCnpj());
            existingPessoa.setCep(request.getCep());
            existingPessoa.setNumero(request.getNumero());
            if (StringUtils.hasText(request.getSenha())) {
                existingPessoa.setSenha(request.getSenha());
            }
            return usuarioRepository.save(existingPessoa);
        }).orElseGet(() -> {
            if (!StringUtils.hasText(request.getSenha())) {
                throw new IllegalArgumentException("A senha é obrigatória para novos usuários.");
            }
            Pessoa newPessoa = new Pessoa();
            newPessoa.setNome(request.getNome());
            newPessoa.setEmail(request.getEmail());
            newPessoa.setIdade(request.getIdade());
            newPessoa.setCpfCnpj(request.getCpfCnpj());
            newPessoa.setCep(request.getCep());
            newPessoa.setNumero(request.getNumero());
            newPessoa.setSenha(request.getSenha());
            return usuarioRepository.save(newPessoa);
        });
    }

    private Veiculo upsertVeiculo(VeiculoSeedDTO veiculoDTO, Pessoa proprietario) {
        return veiculoRepository.findByProprietarioAndPlaca(proprietario, veiculoDTO.getPlaca()).map(existingVeiculo -> {
            existingVeiculo.setModelo(veiculoDTO.getModelo());
            existingVeiculo.setAno(veiculoDTO.getAno());
            return veiculoRepository.save(existingVeiculo);
        }).orElseGet(() -> {
            Veiculo newVeiculo = new Veiculo();
            newVeiculo.setPlaca(veiculoDTO.getPlaca());
            newVeiculo.setModelo(veiculoDTO.getModelo());
            newVeiculo.setAno(veiculoDTO.getAno());
            newVeiculo.setProprietario(proprietario);
            return veiculoRepository.save(newVeiculo);
        });
    }

    private void upsertDadosCalculo(DadosCalculoSeedDTO dadosDTO, Veiculo veiculo) {
        dadosCalculoRepository.findByVeiculoAndMesReferencia(veiculo, dadosDTO.getMesReferencia()).map(existingDados -> {
            existingDados.setKmRodados(dadosDTO.getKmRodados());
            existingDados.setTipoCombustivel(dadosDTO.getTipoCombustivel());
            existingDados.setConsumoMedio(dadosDTO.getConsumoMedio());
            return dadosCalculoRepository.save(existingDados);
        }).orElseGet(() -> {
            DadosCalculo newDados = new DadosCalculo();
            newDados.setKmRodados(dadosDTO.getKmRodados());
            newDados.setTipoCombustivel(dadosDTO.getTipoCombustivel());
            newDados.setConsumoMedio(dadosDTO.getConsumoMedio());
            newDados.setMesReferencia(dadosDTO.getMesReferencia());
            newDados.setVeiculo(veiculo);
            return dadosCalculoRepository.save(newDados);
        });
    }
}
