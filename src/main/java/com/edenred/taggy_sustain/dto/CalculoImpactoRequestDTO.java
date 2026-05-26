package com.edenred.taggy_sustain.dto;

import com.edenred.taggy_sustain.model.FuelType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CalculoImpactoRequestDTO {
    private BigDecimal tFila;
    private BigDecimal consumoMarchaLenta;
    private BigDecimal consumoAdicional;
    private BigDecimal pesoTicket;
    private BigDecimal fatorEmissaoPapel;
    private BigDecimal volumeCarros;
    private FuelType fuelType;
}
