package tcp.server;

import command.ResponseWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class TcpResponseWriter implements ResponseWriter {
    private int len;
    private InputStream stream;
    private final SocketChannel clientChannel;

    public TcpResponseWriter(SocketChannel clientChannel) {
        this.len = 0;
        this.stream = InputStream.nullInputStream();
        this.clientChannel = clientChannel;
    }

    @Override
    public synchronized void write(int len, InputStream inputStream) {
        this.len += len;
        this.stream = new SequenceInputStream(this.stream, inputStream);
    }

    @Override
    public synchronized void write(String text) {
        byte[] buf = text.getBytes(StandardCharsets.UTF_8);
        write(buf.length, new ByteArrayInputStream(buf));
    }

    public synchronized int read(ByteBuffer buffer) throws IOException {
        int bytesRead = Channels.newChannel(stream).read(buffer);
        if (bytesRead > 0) {
            len -= bytesRead;
        }
        return bytesRead;
    }

    public int getLen() {
        return len;
    }

    public SocketChannel getClientChannel() {
        return clientChannel;
    }
}
