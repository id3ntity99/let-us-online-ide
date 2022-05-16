package com.letus.user;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

public class Profile {
    private GoogleIdToken.Payload payload;

    public Profile(GoogleIdToken.Payload payload) {
        this.payload = payload;
    }

    public String getEmail() {
        return payload.getEmail();
    }

    public String getName() {
        return (String) payload.get("name");
    }

    public String getPictureUrl() {
        return (String) payload.get("picture");
    }

    public String getUserId() {
        return payload.getSubject();
    }

    public boolean getEmailVerified() {
        return payload.getEmailVerified();
    }
}
