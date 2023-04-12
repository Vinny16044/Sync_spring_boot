package com.sync.sync.model;

public class ModelNewRequest {

    String clienteId, obrigacaoId,nomeObrigacao,descricao,dataRef,dataVenc,valor,situacaoId,cnpj;

    public ModelNewRequest(String clienteId, String obrigacaoId, String nomeObrigacao, String descricao, String dataRef,
            String dataVenc, String valor, String situacaoId) {
        this.clienteId = clienteId;
        this.obrigacaoId = obrigacaoId;
        this.nomeObrigacao = nomeObrigacao;
        this.descricao = descricao;
        this.dataRef = dataRef;
        this.dataVenc = dataVenc;
        this.valor = valor;
        this.situacaoId = situacaoId;
    }

    

    public ModelNewRequest(String clienteId, String obrigacaoId, String nomeObrigacao, String descricao, String dataRef,
            String dataVenc, String valor, String situacaoId, String cnpj) {
        this.clienteId = clienteId;
        this.obrigacaoId = obrigacaoId;
        this.nomeObrigacao = nomeObrigacao;
        this.descricao = descricao;
        this.dataRef = dataRef;
        this.dataVenc = dataVenc;
        this.valor = valor;
        this.situacaoId = situacaoId;
        this.cnpj = cnpj;
    }



    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getObrigacaoId() {
        return obrigacaoId;
    }

    public void setObrigacaoId(String obrigacaoId) {
        this.obrigacaoId = obrigacaoId;
    }

    public String getNomeObrigacao() {
        return nomeObrigacao;
    }

    public void setNomeObrigacao(String nomeObrigacao) {
        this.nomeObrigacao = nomeObrigacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDataRef() {
        return dataRef;
    }

    public void setDataRef(String dataRef) {
        this.dataRef = dataRef;
    }

    public String getDataVenc() {
        return dataVenc;
    }

    public void setDataVenc(String dataVenc) {
        this.dataVenc = dataVenc;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getSituacaoId() {
        return situacaoId;
    }

    public void setSituacaoId(String situacaoId) {
        this.situacaoId = situacaoId;
    }



    public String getCnpj() {
        return cnpj;
    }



    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    
    
}
