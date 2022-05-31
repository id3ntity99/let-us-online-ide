package com.letus.docker.command;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecStartCmd;
import com.letus.docker.ExecStartResultCallback;
import com.letus.user.User;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.websocket.Session;
import java.io.PipedInputStream;

/**
 * This class is used for starting exec channel connected to a container.
 * Since this class is implementing the Runnable, this class will create a new thread.
 * <p>
 * The reason of implementing Runnable is that ResultCallback will be blocking.
 */
public class StartExecCmd extends AbstractCommand<StartExecCmd, Void> implements Runnable {
    @CheckForNull
    String execId;
    @CheckForNull
    PipedInputStream inputStream;
    @CheckForNull
    Session session;

    /**
     * A method to initialize dockerClient field of the instance.
     *
     * @param dockerClient A docker-api client.
     * @return Returns an object with the initialized dockerClient field.
     */
    public StartExecCmd withDockerClient(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        return this;
    }

    /**
     * A method to initialize execId field of the instance.
     *
     * @param execId A execId used to specify and run exec.
     * @return Returns an object with the initialized execId field.
     */
    public StartExecCmd withExecId(String execId) {
        this.execId = execId;
        return this;
    }


    /**
     * A method to initialize user field of the instance.
     * User object is required to get i/o streams.
     *
     * @param inputStream An input stream for a container to read user's input(keystrokes from frontend).
     * @return Returns an object with the initialized inputStream field.
     */
    public StartExecCmd withStdIn(PipedInputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    public StartExecCmd withSession(Session session) {
        this.session = session;
        return this;
    }

    /**
     * This method overrides Runnable.run().
     */
    @Override
    public void run() {
        ExecStartCmd cmd = dockerClient.execStartCmd(execId);
        ExecStartResultCallback callback = new ExecStartResultCallback(session);
        try {
            cmd.withDetach(false)
                    .withStdIn(inputStream)
                    .withTty(true)
                    .exec(callback)
                    .awaitCompletion();
        } catch (InterruptedException e) {
            logger.error("Cannot start exec to a container...", e);
            Thread.currentThread().interrupt();
        }
    }


    /**
     * Start a new thread that runs a ExecStartResultCallback.
     *
     * @return Returns null.
     */
    @Nullable
    public Void exec() {
        Thread execThread = new Thread(this);
        execThread.setName("execStartThread");
        execThread.start();
        return null;
    }
}
