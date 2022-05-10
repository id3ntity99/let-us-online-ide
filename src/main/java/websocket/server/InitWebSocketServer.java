package websocket.server;

import com.jcraft.jsch.JSch;
import ssh.SSHConnection;
import ssh.SSHExecution;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/init")
public class InitWebSocketServer extends WebSocketServer{
    private final JSch jsch = new JSch();
    private final String host = "172.17.0.2";
    private final String username = "runner";
    private final String pathToKnownHosts = "/home/yosef/.ssh/known_hosts";
    private final String password = "1234";
    private final int port = 22;
    private final String address = "javax.websocket.endpoint.remoteAddress";
    private com.jcraft.jsch.Session sshSession;

    @Override
    @OnOpen
    public void onOpen(Session session) {
        Object clientObj = session.getUserProperties().get(address);
        String clientIP = clientObj.toString();
        if (sshSessionHashMap.containsKey(clientIP)) {
            sshSession = sshSessionHashMap.get(clientIP);
        } else {
            SSHConnection sshConnThread = new SSHConnection(jsch, username, host, port, password, pathToKnownHosts);
            sshConnThread.start();
            sshSession = sshConnThread.getSession();
            sshSessionHashMap.put(clientIP, sshSession);
        }
    }

    @Override
    @OnMessage
    public void onMessage(Session session, String msg) throws Exception {
        try {
            String IP = session.getUserProperties().get(address).toString();
            com.jcraft.jsch.Session sshSession = sshSessionHashMap.get(IP);
            SSHExecution sshExec = new SSHExecution(sshSession);
            sshExec.execute(msg, session);
        } catch (IOException e) {
            LOGGER.log(System.Logger.Level.ERROR, e);
        }
    }
}
