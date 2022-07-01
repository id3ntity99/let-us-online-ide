package com.letus.tokenauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckForNull;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;


public class Auth {
    // TODO
    // FIXME
    //  - CLIENT_ID must be stored in environment variables.
    //  - Current CLIENT_ID is dummy client id.
    private static final Logger logger = LoggerFactory.getLogger(Auth.class);
    private static final String CLIENT_ID = "407408718192.apps.googleusercontent.com";
    private static final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
            .setAudience(Collections.singletonList(CLIENT_ID))
            .build();

    Auth() {
    }

    @CheckForNull
    public static GoogleIdToken.Payload verify(String idTokenString) throws GeneralSecurityException, IOException {
        // Parse string id token to GoogleIdToken
        GoogleIdToken idToken = verifier.verify(idTokenString);
        boolean isVerified = idToken.verify(verifier);
        if (isVerified) {
            return idToken.getPayload();
        }
        return null;
    }
}
