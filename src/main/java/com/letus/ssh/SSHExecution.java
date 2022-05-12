package com.letus.ssh;

import com.jcraft.jsch.ChannelShell;
import com.letus.ssh.exceptions.InputTooLargeException;
import com.letus.user.User;

import javax.websocket.Session;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class SSHExecution {
    private SSHExecution() {}
    private static void writeCommand(OutputStream outputStream,
                                     byte[] cmdBytes) throws IOException, InputTooLargeException {
        if (cmdBytes.length > 2000) {
            throw new InputTooLargeException("Input size cannot exceed 2000 bytes");
        }
        outputStream.write(cmdBytes);
        outputStream.flush();
    }

    private static byte[] readInputStream(InputStream inputStream,
                                          byte[] bufferToWrite) throws IOException {
        Arrays.fill(bufferToWrite, (byte) 0);
        int readBytes = inputStream.read(bufferToWrite);
        if (readBytes < bufferToWrite.length) {
            byte[] trimmedBuffer = new byte[readBytes];
            System.arraycopy(bufferToWrite, 0, trimmedBuffer, 0, readBytes);
            return trimmedBuffer;
        }
        return bufferToWrite;
    }


    public static byte[] readExecResult(byte[] cmd, User user)
            throws InterruptedException, IOException, InputTooLargeException {
        ChannelShell channel = user.getChannel();
        InputStream inputStream = channel.getInputStream();
        OutputStream outputStream = channel.getOutputStream();
        Session clientSession = user.getClientSession();

        writeCommand(outputStream, cmd);

        Thread.sleep(80);
        if (!channel.isConnected() && inputStream.available() <= 0) {
            throw new IOException("Either channel is not connected or input stream is empty");
        }
        byte[] byteBuffer = new byte[512];
        while (channel.isConnected() && inputStream.available() > 0) {
            byteBuffer = readInputStream(inputStream, byteBuffer);
            ByteBuffer buffer = ByteBuffer.wrap(byteBuffer);
            clientSession.getBasicRemote().sendBinary(buffer);
            Thread.sleep(20);
        }
        return byteBuffer;
    }
}