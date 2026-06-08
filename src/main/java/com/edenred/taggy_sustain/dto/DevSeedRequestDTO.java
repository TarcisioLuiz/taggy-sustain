package com.edenred.taggy_sustain.dto;

import lombok.Data;
import java.util.List;

@Data
public class DevSeedRequestDTO {
    private String nome;
    private String email;
    private int idade;
    private String cpfCnpj;
    private String cep;
    private String numero;
    private String senha;

    private List<VeiculoSeedDTO> veiculos;
}
