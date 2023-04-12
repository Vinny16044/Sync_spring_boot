package com.sync.sync.sync;

import java.io.File;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import com.sync.sync.model.ModelNewRequest;
import com.sync.sync.model.ModelRequest;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;



public class PortalServiceClient {

    public static String USERNAME = "vinicius@meucontadoronline.com.br";
    public static String PASSWORD = "67840181Vi";

    public static String BASE_URL = "https://as01.meucontadoronline.com.br/portal/rest";
    // public static String BASE_URL = "http://localhost:8082/portal/rest";
    private static final String AUTH_SERVICE_PATH = "/security/login";
    private static final String VALIDATE_OBRIGACAO_SERVICE_PATH = "/contador/obrigacao/arquivo/validate";
    private static final String CREATE_OBRIGACAO_SERVICE_PATH = "/contador/obrigacao/sincronizar";
    private static final String UPLOAD_FILE_SERVICE_PATH = "/contador/obrigacao/arquivo/upload";

    public boolean validateFile(ModelNewRequest request, ModelRequest requestOld, File file) throws Exception {

        int obrigacaoId = Integer.parseInt(request.getObrigacaoId());
        System.out.println(requestOld.isIsformat());

        if (requestOld.isIsformat() && file.getName().toUpperCase().indexOf("SEM_MOVIMENTO") == -1) {
            int clienteId = Integer.parseInt(request.getClienteId());
            System.out.println(clienteId);
            
            Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
            
            // Autenticação
            String auth = "{\"email\":\"" + USERNAME + "\",\"senha\":\"" + PASSWORD
                    + "\",\"permanecerAutenticado\":false}";
            Response authResponse = client.target(BASE_URL).path(AUTH_SERVICE_PATH).request("application/json")
                .post(Entity.json(auth));
    
            Map<String, NewCookie> cookies = authResponse.getCookies();
    
            if (authResponse.getStatus() != 200
                    && authResponse.getStatus() != 204) {
                throw new Exception("Falha na autenticação: HTTP-" + authResponse.getStatus());
            }
    
            if (file != null && file.exists() && file.isFile()) {
                WebTarget target = client.target(BASE_URL).path(VALIDATE_OBRIGACAO_SERVICE_PATH)
                    .queryParam("obrigacaoId", obrigacaoId)
                    .queryParam("clienteId", clienteId);
    
                final FileDataBodyPart filePart = new FileDataBodyPart("file", file);
                FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
                final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.bodyPart(filePart);
    
                Response validateResponse = target.request()
                    .cookie(cookies.get(requestOld.getJSSESSIONID()))
                    .post(Entity.entity(multipart, multipart.getMediaType()));
    
                formDataMultiPart.close();
                multipart.close();
    
                String output = validateResponse.readEntity(String.class);
    
                System.out.println("POST " + BASE_URL + VALIDATE_OBRIGACAO_SERVICE_PATH);
                System.out.println("Query Param: obrigacaoId=" + String.valueOf(obrigacaoId) + "&clienteId="
                        + String.valueOf(clienteId));
                System.out.println("File attached: " + String.valueOf(file.getName()));
                System.out.println("Response: HTTP-" + validateResponse.getStatus());
                System.out.println(output);
    
                if (validateResponse.getStatus() != 200) {
                    throw new Exception("Falha na validação do arquivo: HTTP-"
                            + validateResponse.getStatus() + " " + output);
                }
    
                return parseOutputStatus(output);
            }
        }
    
        return true;
    }

      

       

    private boolean parseOutputStatus(String output) throws Exception {
        String status = "";

        try {

            if ("{}".equalsIgnoreCase(output.trim())) {
                return false;
            }

            int pos1 = output.indexOf("\"FORMATO_ARQUIVO\":") + "\"FORMATO_ARQUIVO\":".length() + 1;

            if (pos1 > -1) {
                int pos2 = output.indexOf(",", pos1);

                if (pos2 == -1) {
                    pos2 = output.indexOf("}", pos1);
                }

                status = output.substring(pos1, pos2 - 1).trim();
            }

        } catch (Throwable t) {
            System.err.println(output);
            throw new Exception("Status do formato do arquivo n�o retornado: " + t.getMessage());
        }

        return "OK".equalsIgnoreCase(status);
    }

    public void createObrigacao(File file, ModelNewRequest request, ModelRequest requestOld) throws Exception {
        Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        // Autenticacao
        String auth = "{\"email\":\"" + USERNAME + "\",\"senha\":\"" + PASSWORD + "\",\"permanecerAutenticado\":false}";
        System.out.println(auth);
        Response res = client.target(BASE_URL).path(AUTH_SERVICE_PATH).request("application/json")
                .post(Entity.json(auth));

        System.out.println(res);
        String output = res.readEntity(String.class);
        Map<String, NewCookie> cookies = res.getCookies();

        // System.out.println(res.getStatus());
        // System.out.println(output);

        if (res.getStatus() != 200 && res.getStatus() != 204) {
            throw new Exception("Falha na autentica��o: HTTP-" + res.getStatus());
        }

        // Cadastro da Obrigacao
        String input = "{\"id\":0,\"cliente\":{\"id\":" + request.getClienteId() + ",\"cnpj\":\"" + request.getCnpj()
                + "\"},\"obrigacao\":{\"id\":"
                + request.getObrigacaoId() + "},\"dataReferencia\":\""
                + request.getDataRef() + "\",\"dataVencimento\":\"" + request.getDataVenc()
                + "\",\"situacao\":{\"id\":\""
                + request.getSituacaoId() + "\",\"nome\":\"" + "Emitido"
                + "\"},\"valor\": " + request.getValor() + "}";

        System.out.println(input);

        res = client.target(BASE_URL).path(CREATE_OBRIGACAO_SERVICE_PATH).request("application/json")
                .cookie(cookies.get("JSESSIONID")).post(Entity.json(input));

        output = res.readEntity(String.class);

        System.out.println(res.getStatus());
        System.out.println(output);

        if (res.getStatus() != 200 && res.getStatus() != 204) {
            throw new Exception("Falha na gera��o da obriga��o: HTTP-" + res.getStatus());
        }

        int coId = 0;

        try {

            int pos1 = output.indexOf("{\"id\":") + 6;
            int pos2 = output.indexOf(",", pos1);

            String tagValue = output.substring(pos1, pos2).trim();

            coId = Integer.parseInt(tagValue);

        } catch (Throwable t) {
            System.err.println(output);
            throw new Exception("C�digo de Identifica��o de obriga��o criada n�o retornada: " + t.getMessage());
        }

        if (coId == 0) {
            throw new Exception("C�digo de Identifica��o de obriga��o criada inv�lido: " + coId);
        }

        if (file != null && file.exists() && file.isFile()) {

            final FileDataBodyPart filePart = new FileDataBodyPart("file", file);
            FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
            final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.bodyPart(filePart);

            final WebTarget target = client.target(BASE_URL);
            res = target.path(UPLOAD_FILE_SERVICE_PATH).queryParam("id", coId).request()
                    .cookie(cookies.get(requestOld.getJSSESSIONID()))
                    .post(Entity.entity(multipart, multipart.getMediaType()));

            // Use response object to verify upload success

            formDataMultiPart.close();
            multipart.close();

            output = res.readEntity(String.class);

            System.out.println(res.getStatus());
            System.out.println(output);

            if (res.getStatus() != 204) {
                throw new Exception("Falha no upload do arquivo: HTTP-" + res.getStatus() + "  " + output);
            }

            if (res.getStatus() == 204) {

                file.delete();

            }

        }
    }





    
}