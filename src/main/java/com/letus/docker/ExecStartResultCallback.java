package com.letus.docker;

import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.model.Frame;
import com.letus.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ExecStartResultCallback extends ResultCallbackTemplate<ExecStartResultCallback, Frame> {
    private final User user;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecStartResultCallback.class);

    public ExecStartResultCallback(User user) {
        this.user = user;
    }

    @Override
    public void onNext(Frame item) {
        Session session = user.getClientSession();
        if (item != null) {
            try {
                switch (item.getStreamType()) {
                    case STDOUT:
                    case RAW:
                    case STDERR:
                        if (session != null) {
                            byte[] payload = item.getPayload();
                            session.getBasicRemote().sendBinary(ByteBuffer.wrap(payload));
                        }
                        break;
                    default:
                        LOGGER.warn("Unknown stream type: {}", item.getStreamType());
                }
            } catch (IOException e) {
                LOGGER.error(this.getClass().getName() + " raises an exception", e);
            }
        } else {
            LOGGER.warn("Frame doesn't exist");
        }
    }

    @Override
    public void onComplete() {
        LOGGER.info("ResultCallback on complete");
        super.onComplete();
    }
}
