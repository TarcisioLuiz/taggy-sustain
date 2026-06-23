package com.edenred.taggy_sustain.service;

import com.edenred.taggy_sustain.dto.fipe.MarcaDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Service
public class FipeService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BRASIL_API_URL = "https://brasilapi.com.br/api/fipe/marcas/v1/carros";

    public List<MarcaDTO> getMarcas() {
        ResponseEntity<List<MarcaDTO>> response = restTemplate.exchange(
                BRASIL_API_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }
}
