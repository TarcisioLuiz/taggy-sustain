package com.edenred.taggy_sustain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.YearMonth;

@Data
@Entity
public class DadosCalculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double kmRodados;
    private String tipoCombustivel;
    private double consumoMedio;
    private YearMonth mesReferencia;
    //TODO: Adicionar número de passagens pelo pedagio e estacionamento

    @ManyToOne
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;
}
