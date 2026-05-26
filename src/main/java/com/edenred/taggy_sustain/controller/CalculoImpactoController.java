package com.edenred.taggy_sustain.controller;

import com.edenred.taggy_sustain.dto.CalculoSimplificadoRequestDTO;
import com.edenred.taggy_sustain.dto.CalculoSimplificadoResponseDTO;
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
    public ResponseEntity<CalculoSimplificadoResponseDTO> calcularImpactoSimplificado(@RequestBody CalculoSimplificadoRequestDTO request) {
        CalculoSimplificadoResponseDTO response = calculoSimplificadoService.calcularImpactoSimplificado(request);
        return ResponseEntity.ok(response);
    }
}
