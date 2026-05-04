package com.edenred.taggy_sustain.controller;

import com.edenred.taggy_sustain.dto.LoginRequestDTO;
import com.edenred.taggy_sustain.dto.LoginResponseDTO;
import com.edenred.taggy_sustain.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto){
        return ResponseEntity.ok(usuarioService.login(dto));
    }
}
