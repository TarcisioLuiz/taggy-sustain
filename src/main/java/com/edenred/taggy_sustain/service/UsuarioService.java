package com.edenred.taggy_sustain.service;

import com.edenred.taggy_sustain.dto.LoginRequestDTO;
import com.edenred.taggy_sustain.dto.LoginResponseDTO;
import com.edenred.taggy_sustain.model.Pessoa;
import com.edenred.taggy_sustain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public LoginResponseDTO login(LoginRequestDTO dto){
        Pessoa usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!usuario.getSenha().equals(dto.getSenha())){
            throw new RuntimeException("Senha inválida");
        }

        return new LoginResponseDTO(usuario.getId().toString());
    }
}
