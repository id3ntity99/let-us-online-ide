package com.letus.docker;

import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.model.Frame;
import com.letus.user.User;

import javax.websocket.Session;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ExecStartResultCallback extends ResultCallbackTemplate<ExecStartResultCallback, Frame> {
    private Session session;
    private User user;

    public ExecStartResultCallback(User user) {
        this.user = user;
    }

    @Override
    public void onNext(Frame item) {
        session = user.getClientSession();
        if (item != null) {
            try {
                switch (item.getStreamType()) {
                    case STDOUT:
                    case RAW:
                    case STDERR:
                        if (session != null) {
                            byte[] payload = item.getPayload();
                            System.out.println(session.isOpen());
                            System.out.println(session);
                            session.getBasicRemote().sendBinary(ByteBuffer.wrap(payload));
                        }
                        break;
                    default:
                        System.out.println("Unknown stream type: " + item.getStreamType());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Frame doesn't exist");
        }
    }
}
