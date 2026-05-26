package com.edenred.taggy_sustain.service.strategy;

import com.edenred.taggy_sustain.model.FuelType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("DIESEL")
public class DieselEmissionStrategy implements FuelEmissionStrategy {

    @Override
    public BigDecimal getEmissionFactor() {
        return FuelType.DIESEL.getEmissionFactor();
    }
}
