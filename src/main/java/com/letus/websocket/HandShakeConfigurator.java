package com.letus.websocket;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;
import java.util.List;

public class HandShakeConfigurator extends Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig config,
                                HandshakeRequest req,
                                HandshakeResponse res) {
        List<String> idTokenList = req.getParameterMap().get("token");
        config.getUserProperties().put("idToken", idTokenList.get(0));
    }
}