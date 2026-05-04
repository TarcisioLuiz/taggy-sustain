package com.edenred.taggy_sustain;

import com.edenred.taggy_sustain.model.Usuario;
import com.edenred.taggy_sustain.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Data {
    @Bean
    public CommandLineRunner init(UsuarioRepository repo) {
        return args -> {

            if (repo.findByEmail("camila@email.com").isEmpty()) {
                Usuario camila = new Usuario();
                camila.setEmail("camila@email.com");
                camila.setSenha("123456");
                camila.setLogin("usuarioCamila");
                repo.save(camila);
            }

            if (repo.findByEmail("helena@email.com").isEmpty()) {
                Usuario helena = new Usuario();
                helena.setEmail("helena@email.com");
                helena.setSenha("123456");
                helena.setLogin("usuarioHelena");
                repo.save(helena);
            }
        };
    }
}