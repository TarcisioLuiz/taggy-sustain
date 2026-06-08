package com.edenred.taggy_sustain.controller;

import com.edenred.taggy_sustain.dto.B2B.CalculoComparativoB2BResponse;
import com.edenred.taggy_sustain.service.CalculoB2BService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/calculos/b2b")
@RequiredArgsConstructor
public class CalculoB2BController {

    private final CalculoB2BService calculoB2BService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CalculoComparativoB2BResponse>> getCalculosMensais(
        @PathVariable Long usuarioId,
        @RequestParam @DateTimeFormat(pattern="yyyy-MM") YearMonth mes) {

        List<CalculoComparativoB2BResponse> resultados = calculoB2BService.calcularEmissoesMensaisB2B(usuarioId, mes);
        return ResponseEntity.ok(resultados);
    }
}
