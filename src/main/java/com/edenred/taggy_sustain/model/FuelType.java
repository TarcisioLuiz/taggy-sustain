package com.edenred.taggy_sustain.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum FuelType {
    GASOLINA(new BigDecimal("2.33")),
    DIESEL(new BigDecimal("2.62"));

    private final BigDecimal emissionFactor;

    FuelType(BigDecimal emissionFactor) {
        this.emissionFactor = emissionFactor;
    }

}
