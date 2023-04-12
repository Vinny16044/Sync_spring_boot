package com.sync.sync.sync;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HttpPostLoginRequest {

    public String Login() throws Exception {
        // Cria a URL da API
        //URL url = new URL("http://localhost:8082/portal/rest/security/login"); 
        URL url = new URL("https://as01.meucontadoronline.com.br/portal/rest/security/login");
        // Cria uma conexão HTTP
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
    
        // Define o método da requisição como POST
        con.setRequestMethod("POST");
    
        // Define o cabeçalho Content-Type como application/json
        con.setRequestProperty("Content-Type", "application/json");
    
        // Habilita o envio e recebimento de dados
        con.setDoInput(true);
        con.setDoOutput(true);
    
        // Cria o JSON de login
        String jsonInputString = "{\"email\": \"vinicius@meucontadoronline.com.br\", \"senha\": \"67840181Vi\"}";
    
        // Envia o JSON na requisição
        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        wr.write(jsonInputString);
        wr.flush();
    
        // Verifica a resposta do servidor
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Lê a resposta do servidor
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
    
            // Imprime a resposta do servidor
            System.out.println(response.toString());
    
            // Recupera os cookies da resposta
            Map<String, List<String>> headerFields = con.getHeaderFields();
            List<String> cookiesHeader = headerFields.get("Set-Cookie");
            if (cookiesHeader != null) {
                // Extrai o valor do cookie JSESSIONID
                String cookies = cookiesHeader.get(0);
                String sessionId = cookies.split(";")[0].split("=")[1];
                return sessionId;
            }
        } else {
            System.out.println("Erro na requisição. Código de resposta: " + responseCode);
        }
        return null;
    }
}
