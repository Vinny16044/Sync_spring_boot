package com.sync.sync.sync;

/**
 * validateObrigacao
 */
public class validateObrigacao {

    public static String BASE_URL = "https://as01.meucontadoronline.com.br/portal/rest";
	//public static String BASE_URL = "http://localhost:8082/portal/rest";
	private static final String AUTH_SERVICE_PATH = "/security/login";
	private static final String VALIDATE_OBRIGACAO_SERVICE_PATH = "/contador/obrigacao/arquivo/validate";
	private static final String CREATE_OBRIGACAO_SERVICE_PATH = "/contador/obrigacao/sincronizar";
	private static final String UPLOAD_FILE_SERVICE_PATH = "/contador/obrigacao/arquivo/upload";
}