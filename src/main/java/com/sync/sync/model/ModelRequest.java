package com.sync.sync.model;

public class ModelRequest {

    String cliente_id, obrigacao_id, data, JSSESSIONID, filePath, cnpj, nomeObrigacao, descricao, dataFormatada, valor,
            situacaoId, dataRef, dataVenc;

    boolean isLoginIn, isformat, isValidate;

    public String getCnpj() {
        return cnpj;
    }

    public ModelRequest(String cliente_id, String obrigacao_id, String nomeObrigacao, String descricao, String valor,
            String situacaoId, String dataRef, String dataVenc, boolean isLoginIn, boolean isformat,
            boolean isValidate) {
        this.cliente_id = cliente_id;
        this.obrigacao_id = obrigacao_id;
        this.nomeObrigacao = nomeObrigacao;
        this.descricao = descricao;
        this.valor = valor;
        this.situacaoId = situacaoId;
        this.dataRef = dataRef;
        this.dataVenc = dataVenc;
        this.isLoginIn = isLoginIn;
        this.isformat = isformat;
        this.isValidate = isValidate;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public ModelRequest(String cliente_id, String obrigacao_id, String cnpj, String nomeObrigacao, String descricao,
            String valor, String situacaoId, String dataRef, String dataVenc, boolean isLoginIn, boolean isformat,
            boolean isValidate) {
        this.cliente_id = cliente_id;
        this.obrigacao_id = obrigacao_id;
        this.cnpj = cnpj;
        this.nomeObrigacao = nomeObrigacao;
        this.descricao = descricao;
        this.valor = valor;
        this.situacaoId = situacaoId;
        this.dataRef = dataRef;
        this.dataVenc = dataVenc;
        this.isLoginIn = isLoginIn;
        this.isformat = isformat;
        this.isValidate = isValidate;
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

    public void setDataVenc(String ataVenc) {
        this.dataVenc = ataVenc;
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

    public String getDataFormatada() {
        return dataFormatada;
    }

    public void setDataFormatada(String dataFormatada) {
        this.dataFormatada = dataFormatada;
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

    public ModelRequest(String cliente_id, String obrigacao_id, String data, String jSSESSIONID, String filePath,
            String cnpj, String nomeObrigacao, String descricao, String dataFormatada, String valor, String situacaoId,
            boolean isLoginIn, boolean isformat, boolean isValidate) {
        this.cliente_id = cliente_id;
        this.obrigacao_id = obrigacao_id;
        this.data = data;
        JSSESSIONID = jSSESSIONID;
        this.filePath = filePath;
        this.cnpj = cnpj;
        this.nomeObrigacao = nomeObrigacao;
        this.descricao = descricao;
        this.dataFormatada = dataFormatada;
        this.valor = valor;
        this.situacaoId = situacaoId;
        this.isLoginIn = isLoginIn;
        this.isformat = isformat;
        this.isValidate = isValidate;
    }

    public ModelRequest(String cliente_id, String obrigacao_id, String data, String jSSESSIONID, String filePath,
            boolean isLoginIn, boolean isformat, boolean isValidate) {
        this.cliente_id = cliente_id;
        this.obrigacao_id = obrigacao_id;
        this.data = data;
        JSSESSIONID = jSSESSIONID;
        this.filePath = filePath;
        this.isLoginIn = isLoginIn;
        this.isformat = isformat;
        this.isValidate = isValidate;
    }

    public ModelRequest(String cliente_id, String obrigacao_id, String jSSESSIONID, String filePath,
            boolean isLoginIn, boolean isformat) {
        this.cliente_id = cliente_id;
        this.obrigacao_id = obrigacao_id;
        JSSESSIONID = jSSESSIONID;
        this.filePath = filePath;
        this.isLoginIn = isLoginIn;
        this.isformat = isformat;
    }

    public ModelRequest(boolean isformat) {
        this.isformat = isformat;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isValidate() {
        return isValidate;
    }

    public void setValidate(boolean isValidate) {
        this.isValidate = isValidate;
    }

    public ModelRequest(String cliente_id, String obrigacao_id, boolean isLoginIn, boolean isformat) {
        this.cliente_id = cliente_id;
        this.obrigacao_id = obrigacao_id;
        this.isLoginIn = isLoginIn;
        this.isformat = isformat;
    }

    public String getCliente_id() {
        return cliente_id;
    }

    public ModelRequest(String cliente_id, String obrigacao_id, String data, boolean isLoginIn, boolean isformat) {
        this.cliente_id = cliente_id;
        this.obrigacao_id = obrigacao_id;
        this.data = data;
        this.isLoginIn = isLoginIn;
        this.isformat = isformat;
    }

    public void setCliente_id(String cliente_id) {
        this.cliente_id = cliente_id;
    }

    public String getObrigacao_id() {
        return obrigacao_id;
    }

    public void setObrigacao_id(String obrigacao_id) {
        this.obrigacao_id = obrigacao_id;
    }

    public String getJSSESSIONID() {
        return JSSESSIONID;
    }

    public void setJSSESSIONID(String jSSESSIONID) {
        JSSESSIONID = jSSESSIONID;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isLoginIn() {
        return isLoginIn;
    }

    public void setLoginIn(boolean isLoginIn) {
        this.isLoginIn = isLoginIn;
    }

    public boolean isIsformat() {
        return isformat;
    }

    public void setIsformat(boolean isformat) {
        this.isformat = isformat;
    }

}
