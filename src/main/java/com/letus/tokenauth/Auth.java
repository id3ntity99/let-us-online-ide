package com.letus.tokenauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;


public class Auth {
    // TODO
    // FIXME
    //  - CLIENT_ID must be stored in environment variables.
    //  - Current CLIENT_ID is dummy client id.
    private static final String CLIENT_ID = "407408718192.apps.googleusercontent.com";
    private static final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
            .setAudience(Collections.singletonList(CLIENT_ID))
            .build();

    Auth() {
    }

    public static boolean verifyIdToken(String idTokenString) {
        boolean isVerified = false;
        try {
            // Parse string id token to GoogleIdToken
            GoogleIdToken idToken = GoogleIdToken.parse(new GsonFactory(), idTokenString);
            return idToken.verify(verifier);

        } catch (IOException | GeneralSecurityException e) {
            System.out.println(e);
        }
        return isVerified;
    }

    public static GoogleIdToken.Payload getPayLoad(String idTokenString)
            throws IOException, GeneralSecurityException {
        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            return idToken.getPayload();
        } else {
            System.out.println("Invalid ID Token");
        }
        return null;
    }
}
