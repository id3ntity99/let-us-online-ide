package ssh;

import com.jcraft.jsch.ChannelShell;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SSHExecution {
    private final ChannelShell channel;
    private InputStream inputStream;
    private OutputStream outputStream;
    private byte[] byteBuffer = new byte[2000];

    /**
     * Constructor which takes ChannelShell as parameter.
     * This constructor also initialize SSHExecution.inputStream and SSHExecution.outputStream
     * by invoking Channel.getInputStream and Channel.getOutputStream methods.
     *
     * @param channel Existing channel that connects SSH client to SSH server.
     */
    public SSHExecution(ChannelShell channel) throws IOException {
        this.channel = channel;
        this.inputStream = channel.getInputStream();
        this.outputStream = channel.getOutputStream();
    }

    /**
     * Execute remote shell command.
     * This method takes Shell command as a parameter from the websocket.
     * The command received will then be pushed into channel's output stream.
     *
     * @param cmd Shell command to execute remotely.
     */
    public void executeRemoteShell(String cmd) throws IOException {
        outputStream.write(cmd.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }


    /**
     * Read the result of shell execution from the channel's input stream.
     * The data from the input stream will then be stored in the byte array, whose size is 2000.
     * After writing into the byte array, it will be returned as a result of method calling.
     *
     * @return byte[], result of remote shell execution.
     */
    private byte[] readInputStream() throws IOException {
        Arrays.fill(byteBuffer, (byte) 0);
        int readBytes = inputStream.read(byteBuffer, 0, byteBuffer.length);
        if (readBytes != byteBuffer.length) { // if a number of bytes read from inputStream is less than byteBuffer's length
            byte[] trimmedBuffer = new byte[readBytes];
            System.arraycopy(byteBuffer, 0, trimmedBuffer, 0, readBytes);
            byteBuffer = trimmedBuffer;
        }
        return byteBuffer;
    }


    /**
     * Listen to the channel's input stream.
     * If the input stream has bytes to be read, this method will
     * read data from it until there are nothing to read from the input stream.
     * @return byte[], The last read 2000(or less) bytes.
     */
    public byte[] run() throws InterruptedException, IOException {
        if (!channel.isConnected()) {
            throw new IOException("Channel is disconnected");
        }
        if (inputStream.available() <= 0) {
            throw new IOException("InputStream is empty");
        }
        Thread.sleep(100);
        while (channel.isConnected() && inputStream.available() > 0) {
            byte[] byteOutput = readInputStream();
            System.out.print(new String(byteOutput, StandardCharsets.UTF_8));
            Thread.sleep(60);
        }
        return byteBuffer;
    }
}