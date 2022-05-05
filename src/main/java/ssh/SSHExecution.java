package ssh;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import ssh.exceptions.InputTooLargeException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SSHExecution {
    private final ChannelShell channel;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public SSHExecution(Session session) throws IOException, JSchException {
        this.channel = (ChannelShell) session.openChannel("shell");
        channel.setPty(true);
        channel.connect();
        this.inputStream = channel.getInputStream();
        this.outputStream = channel.getOutputStream();
    }

    private void receiveCmd(byte[] cmdBytes) throws IOException, InputTooLargeException {
        if (cmdBytes.length > 2000) {
            throw new InputTooLargeException("Input size cannot exceed 2000 bytes");
        }
        outputStream.write(cmdBytes);
        outputStream.flush();
    }


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


    public byte[] execute(String msg, javax.websocket.Session clientSession)
            throws InputTooLargeException, InterruptedException, IOException {
        receiveCmd(msg.getBytes(StandardCharsets.UTF_8));
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
            ByteBuffer buffer = ByteBuffer.wrap(byteBuffer);
            clientSession.getBasicRemote().sendBinary(buffer);
            // System.out.print(new String(byteBuffer, StandardCharsets.UTF_8));
            Thread.sleep(20);
        }
        channel.disconnect();
        return byteBuffer;
    }
}