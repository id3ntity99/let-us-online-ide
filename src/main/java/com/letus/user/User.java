package com.letus.user;

import com.github.dockerjava.api.model.Container;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.jcraft.jsch.ChannelShell;
import com.letus.tokenauth.Auth;

import javax.websocket.Session;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class User {
    private ChannelShell channel;
    private Session clientSession;
    private Container container;
    private GoogleIdToken.Payload payload;

    public User(UserBuilder builder) {
        this.channel = builder.channel;
        this.container = builder.container;
        this.payload = builder.payload;
        this.clientSession = builder.clientSession;
    }

    public void setClientSession(Session clientSession) {
        if (!this.clientSession.isOpen()) {
            this.clientSession = clientSession;
        }
    }

    public void setChannel(ChannelShell channel) {
        if (!channel.isConnected()) {
            this.channel = channel;
        }
    }

    public Profile getProfile() {
        return new Profile(payload);
    }

    public Container getContainer() {
        return container;
    }

    public ChannelShell getChannel() {
        return channel;
    }

    public Session getClientSession() {
        return clientSession;
    }

    public static class UserBuilder {
        private GoogleIdToken.Payload payload;
        private Session clientSession;
        private ChannelShell channel;
        private Container container;

        public UserBuilder withIdToken(String idToken) {
            try {
                this.payload = Auth.getPayLoad(idToken);
            } catch (IOException | GeneralSecurityException e) {
                System.out.println(e);
            }
            return this;
        }

        public UserBuilder withSshChannel(ChannelShell channel) {
            this.channel = channel;
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

        public User build() {
            return new User(this);
        }
    }
}
