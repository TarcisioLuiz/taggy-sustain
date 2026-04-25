package com.edenred.taggy_sustain.controller;

import com.edenred.taggy_sustain.dto.ImpactoRequestDTO;
import com.edenred.taggy_sustain.dto.ImpactoResponseDTO;
import com.edenred.taggy_sustain.service.CalculoImpactoService;
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
    private CalculoImpactoService calculoImpactoService;

    @PostMapping("/impacto")
    public ResponseEntity<ImpactoResponseDTO> calcularImpacto(@RequestBody ImpactoRequestDTO request) {
        ImpactoResponseDTO response = calculoImpactoService.calcularImpacto(request);
        return ResponseEntity.ok(response);
    }
}
