package com.edenred.taggy_sustain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class CalculoImpactoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoVeiculo;
    private String tipoCombustivel;
    private Integer totalPassagens;

    private Double gramasCo2Evitados;
    private Double percentualReducao;
    private Double arvoresEquivalentes;

    private LocalDateTime dataCalculo;
}
