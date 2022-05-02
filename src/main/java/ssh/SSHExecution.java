package ssh;

import com.jcraft.jsch.ChannelShell;
import ssh.exceptions.InputTooLargeException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SSHExecution {
    private final ChannelShell channel;
    private InputStream inputStream;
    private OutputStream outputStream;

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
     * @param cmdBytes Shell command to execute remotely.
     * @return cmdBytes Return passed arguments if it works properly.
     */
    public int executeRemoteShell(byte[] cmdBytes) throws IOException, InputTooLargeException {
        if (cmdBytes.length > 2000) {
            throw new InputTooLargeException("Input size cannot exceed 2000 bytes");
        }
        outputStream.write(cmdBytes);
        outputStream.flush();
        return cmdBytes.length;
    }


    /**
     * Read the result of shell execution from the channel's input stream.
     * The data from the input stream will then be stored in the byte array, whose size is 2000.
     * After writing into the byte array, it will be returned as a result of method calling.
     *
     * @return byte[], result of remote shell execution.
     */
    private byte[] readInputStream(byte[] bufferToWrite) throws IOException {
        Arrays.fill(bufferToWrite, (byte) 0);
        int readBytes = inputStream.read(bufferToWrite);
        if (readBytes < bufferToWrite.length) {
            byte[] trimmedBuffer = new byte[readBytes];
            System.arraycopy(bufferToWrite, 0, trimmedBuffer, 0, readBytes);
            return trimmedBuffer;
        }
        return bufferToWrite;
    }


    /**
     * Listen to the channel's input stream.
     * If the input stream has bytes to be read, this method will
     * read data from it until there are nothing to read from the input stream.
     *
     * @return byte[], The last-read 2000(or less) bytes.
     */
    public byte[] run() throws InterruptedException, IOException {
        Thread.sleep(100);
        if (!channel.isConnected()) {
            throw new IOException("Channel is disconnected");
        }
        if (inputStream.available() <= 0) {
            throw new IOException("InputStream is empty");
        }
        byte[] byteBuffer = new byte[512];
        while (channel.isConnected() && inputStream.available() > 0) {
            byteBuffer = readInputStream(byteBuffer);
            System.out.print(new String(byteBuffer, StandardCharsets.UTF_8));
            Thread.sleep(20);
        }
        return byteBuffer;
    }

}