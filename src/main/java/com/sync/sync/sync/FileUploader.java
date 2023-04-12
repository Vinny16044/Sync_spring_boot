package com.sync.sync.sync;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.sync.sync.model.ModelRequest;



import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class FileUploader {
    public static String BASE_URL = "http://localhost:8082/portal/rest";
    private static final String AUTH_SERVICE_PATH = "/security/login";
    private static final String VALIDATE_OBRIGACAO_SERVICE_PATH = "/contador/obrigacao/arquivo/validate";
    private static final String CREATE_OBRIGACAO_SERVICE_PATH = "/contador/obrigacao/sincronizar";
    private static final String UPLOAD_FILE_SERVICE_PATH = "/contador/obrigacao/arquivo/upload";

    public ModelRequest Validator(ModelRequest request) throws ClientProtocolException, IOException {
        // Crie um arquivo para enviar
        File fileToUpload = new File(request.getFilePath());

        System.out.println(request.getFilePath());

        String obrigacaoId, clienteId;

        obrigacaoId = request.getObrigacao_id();
        clienteId = request.getCliente_id();

        // Crie a entidade multipart/form-data para enviar os parâmetros e o arquivo
        HttpEntity multipartEntity = MultipartEntityBuilder.create()
            
                .addTextBody("obrigacaoId", String.valueOf(obrigacaoId))
                .addTextBody("clienteId", String.valueOf(clienteId))
                .addBinaryBody("file", fileToUpload, ContentType.DEFAULT_BINARY, fileToUpload.getName())
                .build();

        // Crie a solicitação HTTP POST com a URL e o cookie de sessão
        HashMap<String, String> cookies = new HashMap<>();
        HttpPost httpPost = new HttpPost(BASE_URL + UPLOAD_FILE_SERVICE_PATH);
        httpPost.addHeader("Cookie", "JSESSIONID=" + cookies.get(request.getJSSESSIONID()));
        httpPost.setEntity(multipartEntity);

        // Crie o cliente HTTP e execute a solicitação
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httpPost);

        // Verifique o código de status da resposta e retorne true se for 200
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            String responsePayload = EntityUtils.toString(response.getEntity());
            System.out.println(responsePayload);

            request.setValidate(true);
            return request;
        } else {
            System.out.println("Código de status: " + statusCode);
            request.setValidate(false);
            return request;
        }
    }
}
