package tcp.server;

import auth.Session;
import tcp.utils.BoundedChannelInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ConnectionHandler {
    private static final int BUFFER_SIZE = 4096;
    private static final int INT_BYTE_SIZE = 4;

    private final ByteBuffer inputBuffer;
    private final ByteBuffer outputBuffer;
    private final SocketChannel clientChannel;
    private final Selector selector;
    private final CommandExecutor executor;
    private final Session session;
    private TcpResponseWriter responseWriter;
    private boolean didWroteOutputLen;

    public ConnectionHandler(Selector selector, SocketChannel clientChannel, CommandExecutor executor) {
        this.inputBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.outputBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.clientChannel = clientChannel;
        this.selector = selector;
        this.executor = executor;
        this.session = new Session();
        this.responseWriter = null;
        this.didWroteOutputLen = false;
    }

    public void handleRead(SelectionKey key) {
        if (responseWriter == null) {
            try {
                if (!clientChannel.isOpen()) {
                    handleDisconnect(key);
                }
                int readBytes = clientChannel.read(inputBuffer);
                if (readBytes < 0) {
                    handleDisconnect(key);
                    return;
                }

                inputBuffer.flip();
                String cmd = readInput();

                if (cmd != null) {
                    int streamSize = inputBuffer.getInt();
                    InputStream stream = createInputStream(streamSize);
                    dispatchCommand(cmd, stream);
                } else {
                    inputBuffer.compact();
                }
            } catch (IOException e) {
                handleDisconnect(key);
            }
        }
    }

    private void handleDisconnect(SelectionKey key) {
        try {
            System.out.println("Client disconnected: " + clientChannel.getRemoteAddress());
            clientChannel.close();
            key.cancel();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    private String readInput() {
        if (inputBuffer.remaining() < INT_BYTE_SIZE) {
            return null;
        }
        inputBuffer.mark();
        int cmdLen = inputBuffer.getInt();
        if (inputBuffer.remaining() < cmdLen + INT_BYTE_SIZE) {
            inputBuffer.reset();
            return null;
        }
        byte[] inputBytes = new byte[cmdLen];
        inputBuffer.get(inputBytes);
        return new String(inputBytes, StandardCharsets.UTF_8);
    }

    private InputStream createInputStream(int streamSize) {
        return new BoundedChannelInputStream(clientChannel, inputBuffer, streamSize);
    }

    private void dispatchCommand(String cmd, InputStream stream) {
        this.responseWriter = new TcpResponseWriter(clientChannel);
        clientChannel.keyFor(selector).interestOps(0);
        executor.executeCommand(responseWriter, cmd, stream, session);
    }

    public void handleWrite(SelectionKey key) {
        if (responseWriter != null) {
            try {
                if (!didWroteOutputLen) {
                    if (outputBuffer.remaining() < INT_BYTE_SIZE) {
                        return;
                    }
                    didWroteOutputLen = true;
                    outputBuffer.putInt(responseWriter.getLen());
                }
                int readBytes = responseWriter.read(outputBuffer);
                if (readBytes < 0) {
                    handleOutputEOF(key);
                    return;
                }
                outputBuffer.flip();
                int writtenBytes = clientChannel.write(outputBuffer);
                if (writtenBytes < 0) {
                    handleDisconnect(key);
                    return;
                }
                outputBuffer.compact();
            } catch (IOException e) {
                handleDisconnect(key);
            }
        }
    }

    private void handleOutputEOF(SelectionKey key) {
        this.responseWriter = null;
        this.didWroteOutputLen = false;
        String cmd = readInput();
        if (cmd != null) {
            key.interestOps(0);
            int streamSize = inputBuffer.getInt();
            InputStream stream = createInputStream(streamSize);
            dispatchCommand(cmd, stream);
        } else {
            inputBuffer.compact();
            key.interestOps(SelectionKey.OP_READ);
        }
    }
}
