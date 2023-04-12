package com.sync.sync.sync;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.io.File;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Base64;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.json.JSONArray;
import org.json.JSONObject;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sync.sync.model.ModelNewRequest;
import com.sync.sync.model.ModelRequest;




public class PortalApiService {

	// public static String USERNAME = "pim@meucontadoronline.com.br";
	// public static String PASSWORD = "Mco#123!";
	public static String USERNAME = "vinicius@meucontadoronline.com.br";
    public static String PASSWORD = "67840181Vi";
	public static String BASE_URL = "https://as01.meucontadoronline.com.br/portal/rest";
	// public static String PORTAL_URL = "http://localhost:8080/portal/rest";
	public static String BATCH_URL = "https://portal.meucontadoronline.com.br/batch-server/rest";
	public static String PORTAL_URL = "https://portal.meucontadoronline.com.br/portal/rest";
	public static String MERP_URL = "https://merp.meucontadoronline.com.br/merp/rest";
	private static final String AUTH_SERVICE_PATH = "/security/login";

	private static final String CREATE_OBRIGACAO_SERVICE_PATH = "/contador/obrigacao/sincronizar";

	public JSONObject callCreateObrigacao(File file, ModelNewRequest request, ModelRequest requestOld)
			throws Exception {


		
		Instant instant = Instant.parse(request.getDataRef());
		int mes = instant.atZone(ZoneId.of("UTC")).getMonthValue();
		int ano = instant.atZone(ZoneId.of("UTC")).getYear() % 100;
		String dataRef = (String.format("%02d-%02d", mes, ano));
		//System.out.println(dataRef);
		byte[] fileBytes = Files.readAllBytes(file.toPath());
		

		// Convertendo o array de bytes em uma string Base64
		String base64EncodedFile = Base64.getEncoder().encodeToString(fileBytes);

		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
		String auth = "{\"email\":\"" + USERNAME + "\",\"senha\":\"" + PASSWORD + "\",\"permanecerAutenticado\":false}";
		Response res = client.target(BASE_URL).path(AUTH_SERVICE_PATH).request("application/json")
				.post(Entity.json(auth));
		Map<String, NewCookie> cookies = res.getCookies();

		JSONObject param = new JSONObject();

		JSONObject cliente = new JSONObject();
		JSONObject obrigacao = new JSONObject();
		JSONObject situacao = new JSONObject();
		JSONObject motivo = new JSONObject();
		JSONArray arquivos = new JSONArray();
		JSONObject arquivo = new JSONObject();

		cliente.put("id", request.getClienteId());
		param.put("cliente", cliente);
		obrigacao.put("id", request.getObrigacaoId());
		param.put("obrigacao", obrigacao);
		param.put("dataReferencia", request.getDataRef());
		param.put("dataVencimento", request.getDataVenc());

		situacao.put("id", request.getSituacaoId());
		situacao.put("nome", "Emitido");
		param.put("situacao", situacao);

		motivo.put("nome", request.getNomeObrigacao());
		param.put("motivo", motivo);
		param.put("nome", request.getNomeObrigacao());
		param.put("descricao", request.getDescricao());
		param.put("valor", request.getValor());
		
		arquivo.put("nome", file.getName());
		arquivo.put("tipo", "pdf");
		arquivo.put("conteudo", base64EncodedFile);
		arquivos.put(arquivo);
		param.put("arquivos", arquivos);
		param.put("limparArquivos", false);
		// if (ctx.containsKey("limparArquivos") && "true".equalsIgnoreCase((String)
		// ctx.get("limparArquivos"))) {
		// param.put("limparArquivos", true);
		// }

		//System.out.println(param.toString());

		String output = callCreateObrigacao(client, cookies, param);
		int coId = 0;

		try {

			int pos1 = output.indexOf("{\"id\":") + 6;
			int pos2 = output.indexOf(",", pos1);
 
			String tagValue = output.substring(pos1, pos2).trim() ;

			coId = Integer.parseInt(tagValue);

			param.put("id", coId);

		} catch (Throwable t) {
			System.err.println(output);
			throw new Exception("Codigo de Identificação de obrigação criada não retornada: " + t.getMessage());
		}

		if (coId == 0) {
			throw new Exception("Código de Identificação de obrigação criada inválido: " + coId);
		}

		return param;
	}

	private String callCreateObrigacao(Client client, Map<String, NewCookie> cookies, JSONObject param)
			throws Exception {

		Response response = client.target(PORTAL_URL).path(CREATE_OBRIGACAO_SERVICE_PATH).request("application/json")
				.cookie(cookies.get("JSESSIONID")).post(Entity.json(param.toString()));

		String output = response.readEntity(String.class);

		if (response.getStatus() != 200 && response.getStatus() != 204) {
			System.err.println("HTTP-" + response.getStatus() + "\n" + output);
			throw new Exception("Falha na geração da obrigação: HTTP-" + response.getStatus());
		}

		return output;
	}

}
