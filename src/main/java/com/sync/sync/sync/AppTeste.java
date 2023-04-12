package com.sync.sync.sync;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.sync.sync.model.ModelRequest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AppTeste {
    // Constantes usadas para configurar a API do Google Drive
    private static final String APPLICATION_NAME = "asdasdadasdad";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "src/main/java/com/credenciais/client_secret_788535055623-7r0fdj2np11dgu2vgc2q3q3f27nfclk2.apps.googleusercontent.com.json";
    private static boolean isValidate;
    private static boolean validateFile;

    // Método usado para obter as credenciais para acessar a API do Google Drive
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Carrega o arquivo com as credenciais
        InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Configura o fluxo de autorização usando as credenciais carregadas
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES).setDataStoreFactory(new FileDataStoreFactory(new java.io.File("token")))
                .setAccessType("offline").build();

        // Realiza a autorização e retorna as credenciais
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static void main(String[] args) throws Exception {
        // Cria um novo cliente para a API do Google Drive
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME).build();

        // ID da pasta que deve ser sincronizada
        String folderSync = "1RH0S8Q31NwDnsG1ZBC0wrWgCMXjAcfJG";

        FileList result = service.files().list().setQ("'" + folderSync + "' in parents and trashed = false ")
                .setFields("nextPageToken, files(id, name)").execute();

        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("Nenhum arquivo localizado.");
        } else {
            System.out.println("Pastas:");
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);

                System.out.println("Arquivo rais " + i + ": " + file.getName());

                // Lista arquivo dento da pasta

                String folderId = file.getId();

                String query = "'" + folderId + "' in parents and trashed = false";

                FileList fileList = service.files().list().setQ(query).setFields("nextPageToken, files(id, name)")
                        .execute();
                List<File> filesInFolder = fileList.getFiles();
                if (filesInFolder == null || filesInFolder.isEmpty()) {
                    System.out.println("Não há arquivos na pasta." + file.getName());
                } else {
                    System.out.println("Arquivos dentro da pasta :" + file.getName());

                    for (int j = 0; j < filesInFolder.size(); j++) {
                        File file1 = filesInFolder.get(j);
                        System.out.println("- " + file1.getName() + "-origem da pasta" + file.getName());

                        String folderId1 = file1.getId();

                        String subQuery = "'" + folderId1 + "' in parents and trashed = false";
                        FileList fileList1 = service.files().list().setQ(subQuery)
                                .setFields("nextPageToken, files(id, name)")
                                .execute();
                        List<File> filesInFolderinFolder = fileList1.getFiles();
                        if (filesInFolderinFolder == null || filesInFolderinFolder.isEmpty()) {
                            System.out.println("Não há arquivos na pasta." + file1.getName());
                        } else {
                            System.out.println("Arquivos dentro da pasta :" + file1.getName());

                            for (int k = 0; k < filesInFolderinFolder.size(); k++) {
                                File file2 = filesInFolderinFolder.get(k);
                                System.out.println("- " + file2.getName());
                                if (file2.getName().toString().endsWith(".pdf")
                                        || file2.getName().toString().endsWith(".PDF")) {
                                   

                                        JobValidate validate = new JobValidate();

                                        String pastaData = file1.getName();
                                        String pastaObg = file.getName();
                                        System.out.println(pastaObg);

                                        ModelRequest resquest = validate.Format(pastaData, file2.getName());

                                        if (resquest.isIsformat()) {

                                            System.out.println("Arquivo " + k + ": " + file2.getName());

                                            File filetodown = filesInFolderinFolder.get(k);
                                            String fileId = filetodown.getId();
                                            String fileNome = filetodown.getName();
                                            java.io.File localDirectory = new java.io.File(
                                                    "pasta_de_arquivos_temporarios");
                                            if (!localDirectory.exists()) {
                                                localDirectory.mkdirs();
                                            }

                                            java.io.File localFile = new java.io.File(localDirectory, file2.getName());

                                            OutputStream outputStream = new FileOutputStream(localFile);

                                            service.files().get(fileId).executeMediaAndDownloadTo(outputStream);

                                            outputStream.close();

                                            java.io.File downloadedFile = new java.io.File(localFile.getAbsolutePath());

                                            System.out.println(
                                                    "o arquivo: " + fileNome + " esta no diretorio especificado:"
                                                            + localDirectory);

                                            HttpPostLoginRequest login = new HttpPostLoginRequest();

                                            String JSSESSIONID = login.Login();

                                            resquest.setJSSESSIONID(JSSESSIONID);

                                            if (JSSESSIONID != null) {

                                                FileScanJob job = new FileScanJob();

                                                job.processFile(downloadedFile, resquest);
                                                try {
                                                    String renomear = file2.getName().substring(0,
                                                            file2.getName().lastIndexOf('.'))
                                                            + ".LOADED.PDF";

                                                    file2.setName(renomear);

                                                    File fileMetadata = new File();
                                                    fileMetadata.setName(renomear);

                                                    // Remove o campo problemático
                                                    fileMetadata.setProperties(null);

                                                    // Chamar o método update do objeto Drive.Files para atualizar o
                                                    // nome do
                                                    // arquivo
                                                    File updatedFile = service.files()
                                                            .update(file2.getId(), fileMetadata)
                                                            .execute();
                                                    System.out.println(
                                                            "Arquivo renomeado para: " + updatedFile.getName());

                                                } catch (IOException e) {
                                                    System.out.println(
                                                            "Erro ao ler/escrever o arquivo: " + e.getMessage());
                                                } catch (Exception e) {
                                                    if (e instanceof GoogleJsonResponseException) {
                                                        GoogleJsonResponseException gjre = (GoogleJsonResponseException) e;
                                                        System.out.println(
                                                                "Erro de API do Google: "
                                                                        + gjre.getDetails().getMessage());
                                                    } else {
                                                        System.out.println("Erro não esperado: " + e.getMessage());
                                                    }
                                                }

                                            }

                                        }

                                        else {

                                            if (file2.getName().contains(".LOADED") || file2.getName().contains(".FormatoInvalido")) {

                                                System.out.println("obrigação ja tratada");

                                            } else {
                                                try {
                                                    String renomear = file2.getName().substring(0,
                                                            file2.getName().lastIndexOf('.'))
                                                            + ".FormatoInvalido.PDF";

                                                    file2.setName(renomear);

                                                    File fileMetadata = new File();
                                                    fileMetadata.setName(renomear);

                                                    // Remove o campo problemático
                                                    fileMetadata.setProperties(null);

                                                    // Chamar o método update do objeto Drive.Files para atualizar o
                                                    // nome do
                                                    // arquivo
                                                    File updatedFile = service.files()
                                                            .update(file2.getId(), fileMetadata)
                                                            .execute();
                                                    System.out.println(
                                                            "Arquivo renomeado para: " + updatedFile.getName());

                                                } catch (IOException e) {
                                                    System.out.println(
                                                            "Erro ao ler/escrever o arquivo: " + e.getMessage());
                                                } catch (Exception e) {
                                                    if (e instanceof GoogleJsonResponseException) {
                                                        GoogleJsonResponseException gjre = (GoogleJsonResponseException) e;
                                                        System.out.println(
                                                                "Erro de API do Google: "
                                                                        + gjre.getDetails().getMessage());
                                                    } else {
                                                        System.out.println("Erro não esperado: " + e.getMessage());
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }
                            }

                        }
                    }
                
            }
        }
    }
}