package com.edenred.taggy_sustain.domain;

public class Usuario {

    private Long id;

    private String tipoVeiculo;
    private String tipoCombustivel;
    private Integer totalPassagens;

    public Usuario(long id, String tipoVeiculo, String tipoCombustivel, Integer totalPassagens) {
        this.id = id;
        this.tipoVeiculo = tipoVeiculo;
        this.tipoCombustivel = tipoCombustivel;
        this.totalPassagens = totalPassagens;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(String tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }

    public String getTipoCombustivel() {
        return tipoCombustivel;
    }

    public void setTipoCombustivel(String tipoCombustivel) {
        this.tipoCombustivel = tipoCombustivel;
    }

    public Integer getTotalPassagens() {
        return totalPassagens;
    }

    public void setTotalPassagens(Integer totalPassagens) {
        this.totalPassagens = totalPassagens;
    }
}