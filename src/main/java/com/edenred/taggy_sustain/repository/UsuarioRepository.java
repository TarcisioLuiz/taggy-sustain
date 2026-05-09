package com.edenred.taggy_sustain.repository;

import com.edenred.taggy_sustain.domain.Usuario;
// !!!!!! A classe recomendada para pegar os dados do banco e transformar na sua List<Usuario> para usar no programa !!!!!

// Importações necessárias para o JDBC funcionar
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {

    // Configurações do seu banco de dados (ajuste conforme o seu)
    private static final String URL = "jdbc:mysql://localhost:3306/meu_projeto";
    private static final String USER = "root";
    private static final String PASSWORD = "sua_senha_aqui";

    // Método que vai no banco, pega tudo e transforma na sua List<Usuario>
    public List<Usuario> buscarTodosNoBanco() {
        List<Usuario> listaDeUsuarios = new ArrayList<>();
        String sql = "SELECT id, tipoVeiculo, tipoCombustivel, totalPassagens FROM usuarios";

        // O bloco try-with-resources garante que a conexão será fechada no final
        try (Connection conexao = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet tabelaResultado = comando.executeQuery()) {

            // Enquanto houver uma próxima linha no resultado do banco...
            while (tabelaResultado.next()) {
                // 1. Pegamos os dados da linha atual
                long idBanco = tabelaResultado.getLong("id");
                String tipoVeiculoBanco = tabelaResultado.getString("tipoVeiculo");
                String tipoCombustivelBanco = tabelaResultado.getString("tipoCombustivel");
                Integer totalPassagensBanco = tabelaResultado.getInt("totalPassagens");

                // 2. Criamos o nosso objeto Java com esses dados
                Usuario usuario = new Usuario(idBanco, tipoVeiculoBanco, tipoCombustivelBanco, totalPassagensBanco);

                // 3. Adicionamos na lista
                listaDeUsuarios.add(usuario);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar dados no banco: " + e.getMessage());
        }

        // Retorna a lista pronta para ser usada no seu programa
        return listaDeUsuarios;
    }
}