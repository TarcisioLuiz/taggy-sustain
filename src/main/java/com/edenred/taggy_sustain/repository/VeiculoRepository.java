package com.edenred.taggy_sustain.repository;

import com.edenred.taggy_sustain.model.Pessoa;
import com.edenred.taggy_sustain.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    List<Veiculo> findByProprietarioId(Long proprietarioId);
    Optional<Veiculo> findByProprietarioAndPlaca(Pessoa proprietario, String placa);
}
