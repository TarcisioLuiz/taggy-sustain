package com.edenred.taggy_sustain.controller;

import com.edenred.taggy_sustain.dto.fipe.MarcaDTO;
import com.edenred.taggy_sustain.service.FipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fipe")
public class FipeController {

    @Autowired
    private FipeService fipeService;

    @GetMapping("/marcas/carros")
    public ResponseEntity<List<MarcaDTO>> getMarcasCarros() {
        return ResponseEntity.ok(fipeService.getMarcas());
    }
}
