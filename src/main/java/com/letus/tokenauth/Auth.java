package com.letus.tokenauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Auth {
    // TODO
    // FIXME
    //  - CLIENT_ID must be stored in environment variables.
    private static final String CLIENT_ID = "";
    private final GoogleIdTokenVerifier verifier;

    public Auth() {
        HttpTransport transport = Utils.getDefaultTransport();
        JsonFactory jsonFactory = Utils.getDefaultJsonFactory();
        this.verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
    }

    public Map<String, String> verify(String idTokenString) throws IOException, GeneralSecurityException {
        Map<String, String> clientInfo = new HashMap<>();
        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String userId = payload.getSubject();
            String email = payload.getEmail();
            boolean isEmailVerified = payload.getEmailVerified();
            String name = (String) payload.get("name");
            String picUrl = (String) payload.get("picture");
            clientInfo.put("userId", userId);
            clientInfo.put("email", email);
            clientInfo.put("isEmailVerified", String.valueOf(isEmailVerified));
            clientInfo.put("name", name);
            clientInfo.put("pictureUrl", picUrl);
        } else {
            System.out.println("Invalid ID Token");
        }
        return clientInfo;
    }
}
