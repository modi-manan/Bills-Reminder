package org.manan.authorization;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.tasks.TasksScopes;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Singleton class Responsible for taking care of authentication for the application.
 * @author Manan Modi
 */
public class Credentials {

    private static final String CREDENTIALS_FILE_PATH = "/credentials.config.json";
    public static final String TOKENS_DIRECTORY_PATH = System.getProperty("user.home") + File.separator + "/token";
    public static final GsonFactory GSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.unmodifiableList(Arrays.asList(GmailScopes.GMAIL_READONLY, TasksScopes.TASKS));
    private static Credentials credentials = null;
    private static GoogleAuthorizationCodeFlow flow;
    private static NetHttpTransport HTTP_TRANSPORT;

    private Credentials() throws GeneralSecurityException, IOException {
        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        InputStream in = Credentials.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(GSON_FACTORY, new InputStreamReader(in));
        flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, GSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
    }

    public static Credentials getInstance() throws GeneralSecurityException, IOException {
        if(credentials == null){
            credentials = new Credentials();
        }
        return credentials;
    }

    private Credential authorizeCredentials() throws IOException {
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public Credential getCredential() throws IOException {
        Credential credential = flow.loadCredential("user");
        if(credential == null){
            credential = authorizeCredentials();
        }
        return credential;
    }

}
