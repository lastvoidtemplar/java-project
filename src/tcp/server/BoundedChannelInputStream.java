package tcp.server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class BoundedChannelInputStream extends InputStream {
    private final SocketChannel clientChannel;
    private final ByteBuffer buffer;
    private int remaining;

    public BoundedChannelInputStream(SocketChannel clientChannel, ByteBuffer buffer, int size) {
        this.clientChannel = clientChannel;
        this.buffer = buffer;
        this.remaining = size;
    }

    @Override
    public int read() throws IOException {
        byte[] one = new byte[1];
        int r = read(one, 0, 1);
        return r == -1 ? -1 : one[0] & 0xff;
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
