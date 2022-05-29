package com.letus.user;

import com.github.dockerjava.api.model.Container;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.letus.tokenauth.Auth;

import javax.websocket.Session;
import java.io.*;
import java.security.GeneralSecurityException;

public class User implements Closeable {
    private Session clientSession;
    private Container container;
    private GoogleIdToken.Payload payload;
    private PipedInputStream in;
    private PipedOutputStream out;

    public User(UserBuilder builder) {
        this.container = builder.container;
        this.payload = builder.payload;
        this.clientSession = builder.clientSession;
        this.in = builder.in;
        this.out = builder.out;
    }

    public void setClientSession(Session clientSession) {
        if (!this.clientSession.isOpen()) {
            this.clientSession = clientSession;
        }
    }

    public Profile getProfile() {
        return new Profile(payload);
    }

    public Container getContainer() {
        return container;
    }

    public Session getClientSession() {
        return clientSession;
    }

    public PipedInputStream getInputStream() {
        return in;
    }

    public PipedOutputStream getOutputStream() {
        return out;
    }

    @Override
    public void close() {
        try {
            this.clientSession.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static class UserBuilder {
        private GoogleIdToken.Payload payload;
        private Session clientSession;
        private Container container;
        private PipedInputStream in;
        private PipedOutputStream out;

        public UserBuilder withIdToken(String idToken) {
            try {
                this.payload = Auth.getPayLoad(idToken);
            } catch (IOException | GeneralSecurityException e) {
                e.printStackTrace();
            }
            return this;
        }

        public UserBuilder withContainer(Container container) {
            this.container = container;
            return this;
        }

        public UserBuilder withClientSession(Session session) {
            this.clientSession = session;
            return this;
        }

        public UserBuilder withInputStream(PipedInputStream in) {
            this.in = in;
            if (this.out != null) {
                try {
                    in.connect(out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return this;
        }

        public UserBuilder withOutputStream(PipedOutputStream out) {
            this.out = out;
            if (this.in != null) {
                try {
                    out.connect(in);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
