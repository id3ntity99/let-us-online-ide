package com.letus.tokenauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.letus.user.UserInfo;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class Auth {
    // TODO
    // FIXME
    //  - CLIENT_ID must be stored in environment variables.
    //  - Current CLIENT_ID is dummy client id.
    private static final String CLIENT_ID = "407408718192.apps.googleusercontent.com";
    private final GoogleIdTokenVerifier verifier;

    public Auth() {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
    }

    public boolean verifyIdToken(String idTokenString) {
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

    public UserInfo getInfo(String idTokenString) throws IOException, GeneralSecurityException {
        GoogleIdToken idToken = verifier.verify(idTokenString);
        UserInfo userInfo;
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String userId = payload.getSubject();
            String email = payload.getEmail();
            boolean isEmailVerified = payload.getEmailVerified();
            String name = (String) payload.get("name");
            String picUrl = (String) payload.get("picture");
            userInfo = new UserInfo(userId, email, isEmailVerified, name, picUrl);
            return userInfo;
        } else {
            System.out.println("Invalid ID Token");
        }
        return null;
    }
}
