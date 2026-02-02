package tcp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class BoundedChannelInputStream extends InputStream {
    private static final int BYTE_MASK = 0xff;

    private final SocketChannel clientChannel;
    private final ByteBuffer buffer;
    private int remaining;

    public BoundedChannelInputStream(SocketChannel clientChannel, ByteBuffer buffer, int size) {
        this.clientChannel = clientChannel;
        this.buffer = buffer;
        this.remaining = size;
    }

    public int getRemaining() {
        return remaining;
    }

    @Override
    public int read() throws IOException {
        byte[] b = new byte[1];
        int read = read(b, 0, 1);
        if (read == -1) {
            return read;
        }
        return b[0] & BYTE_MASK;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (remaining <= 0) return -1;

        len = (int) Math.min(len, remaining);
        if (buffer.hasRemaining()) {
            int read = (int) Math.min(len, buffer.remaining());
            buffer.get(b, off, read);
            remaining -= read;
            return read;
        }
        
        ByteBuffer dst = ByteBuffer.wrap(b, off, len);

        int read = clientChannel.read(dst);
        if (read <= 0) return read;

        remaining -= read;
        return read;
    }
}
