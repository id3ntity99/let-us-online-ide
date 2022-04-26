package ssh;

import com.jcraft.jsch.ChannelShell;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.IntToDoubleFunction;

public class SSHExecution {
    private final ChannelShell channel;
    private InputStream inputStream;
    private OutputStream outputStream;
    private static final System.Logger LOGGER = System.getLogger(SSHExecution.class.getName());

    /**
     * Constructor which takes ChannelShell as parameter.
     * This constructor also initialize SSHExecution.inputStream and SSHExecution.outputStream
     * by invoking Channel.getInputStream and Channel.getOutputStream methods.
     *
     * @param channel Existing channel that connects SSH client to SSH server.
     */
    public SSHExecution(ChannelShell channel) {
        this.channel = channel;
        try {
            this.inputStream = channel.getInputStream();
            this.outputStream = channel.getOutputStream();
        } catch (IOException e) {
            LOGGER.log(System.Logger.Level.ERROR, e);
        }
    }

    /**
     * Execute remote shell command.
     * This method takes Shell command as a parameter from the websocket.
     * The command received will then be pushed into channel's output stream.
     *
     * @param cmd Shell command to execute remotely.
     */
    public void executeRemoteShell(String cmd) {
        try {
            outputStream.write(cmd.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            LOGGER.log(System.Logger.Level.ERROR, e);
        }
    }


    /**
     * Read the result of shell execution from the channel's input stream.
     * The data from the input stream will then be stored in the byte array, whose size is 8192.
     * After writing into the byte array, it will be returned as a result of method calling.
     *
     * @return byte[], result of remote shell execution.
     */
    private byte[] readInputStream() {
        byte[] byteBuffer = new byte[2000];
        try {
            inputStream.read(byteBuffer, 0, byteBuffer.length);
        } catch (IOException e) {
            LOGGER.log(System.Logger.Level.ERROR, e);
        }
        return byteBuffer;
    }


    /**
     * Listen to the channel's input stream.
     * If the input stream has bytes to be read, this method will
     * read data from it until there are nothing to read from the input stream.
     */
    public void run() {
        try {
            Thread.sleep(50);
            do {
                byte[] byteOutput = readInputStream();
                System.out.print(new String(byteOutput, StandardCharsets.UTF_8));
                Thread.sleep(60);
            }
            while (channel.isConnected() && inputStream.available() > 0);
        } catch (InterruptedException e) {
            LOGGER.log(System.Logger.Level.ERROR, e);
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            LOGGER.log(System.Logger.Level.ERROR, e);
        }
    }
}
