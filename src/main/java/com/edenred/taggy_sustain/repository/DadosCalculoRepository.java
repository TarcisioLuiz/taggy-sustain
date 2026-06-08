package com.edenred.taggy_sustain.repository;

import com.edenred.taggy_sustain.model.DadosCalculo;
import com.edenred.taggy_sustain.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.Optional;

@Repository
public interface DadosCalculoRepository extends JpaRepository<DadosCalculo, Long> {
    Optional<DadosCalculo> findByVeiculoAndMesReferencia(Veiculo veiculo, YearMonth mesReferencia);
}
