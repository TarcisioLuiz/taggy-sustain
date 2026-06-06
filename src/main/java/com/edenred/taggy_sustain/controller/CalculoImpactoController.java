package com.edenred.taggy_sustain.controller;

import com.edenred.taggy_sustain.dto.CalculoB2CRequest;
import com.edenred.taggy_sustain.dto.CalculoB2CResponse;
import com.edenred.taggy_sustain.service.CalculoSimplificadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/calculo")
public class CalculoImpactoController {

    @Autowired
    private CalculoSimplificadoService calculoSimplificadoService;

    @PostMapping("/impacto-simplificado")
    public ResponseEntity<CalculoB2CResponse> calcularImpactoSimplificado(@RequestBody CalculoB2CRequest request) {
        CalculoB2CResponse response = calculoSimplificadoService.calcularImpactoSimplificado(request);
        return ResponseEntity.ok(response);
    }
}
