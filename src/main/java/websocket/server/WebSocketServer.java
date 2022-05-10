package websocket.server;

import com.jcraft.jsch.JSchException;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;

abstract public class WebSocketServer {
    protected static final System.Logger LOGGER = System.getLogger(WebSocketServer.class.getName());
    protected static final HashMap<String, com.jcraft.jsch.Session> sshSessionHashMap = new HashMap<>();

    @OnOpen
    public abstract void onOpen(Session session) throws IOException, JSchException;

    @OnMessage
    public abstract void onMessage(Session session, String msg) throws Exception;

}
