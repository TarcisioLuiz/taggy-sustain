package com.edenred.taggy_sustain.controller;

import com.edenred.taggy_sustain.dto.DevSeedRequestDTO;
import com.edenred.taggy_sustain.service.DevSeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class DevController {

    private final DevSeedService devSeedService;

    @PostMapping("/seed")
    public ResponseEntity<String> seedDatabase(@RequestBody DevSeedRequestDTO request) {
        try {
            devSeedService.seedDatabase(request);
            return ResponseEntity.ok("Banco de dados populado com sucesso para o email: " + request.getEmail());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Falha ao popular o banco de dados: " + e.getMessage());
        }
    }
}
