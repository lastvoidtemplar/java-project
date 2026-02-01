package tcp.server;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class TcpServer {
    private static final String HOST = "localhost";

    private final int port;
    private boolean isRunning;
    private Selector selector;
    private CommandExecutor executor;

    public TcpServer(int port) {
        this.port = port;
        this.isRunning = false;
    }

    public void start() {
        try (ServerSocketChannel serverChannel = configureServerChannel()) {
            executor = new CommandExecutor(selector);
            isRunning = true;
            while (isRunning) {
                int countSelectedKeys = selector.select();
                handleReadyTcpResponseWriters();
                if (countSelectedKeys == 0) {
                    continue;
                }
                handleSelectedKeys();
            }
            selector.close();
        } catch (IOException e) {
            throw new UncheckedIOException("IOException was thrown, while starting the server", e);
        }
    }

    private void handleSelectedKeys() {
        var selectedKeys = selector.selectedKeys().iterator();
        while (selectedKeys.hasNext()) {
            SelectionKey key = selectedKeys.next();
            selectedKeys.remove();
            if (key.isAcceptable()) {
                accept(key);
            } else if (key.isReadable()) {
                read(key);
            } else if (key.isWritable()) {
                write(key);
            }
        }
    }

    private ServerSocketChannel configureServerChannel() throws IOException {
        selector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(HOST, port));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server started on " + serverChannel.getLocalAddress());
        return serverChannel;
    }

    private void accept(SelectionKey key) {
        try {
            ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
            SocketChannel clientChannel = serverChannel.accept();

            if (clientChannel == null) {
                return;
            }
            clientChannel.configureBlocking(false);
            ConnectionHandler handler = new ConnectionHandler(selector, clientChannel, executor);
            clientChannel.register(selector, SelectionKey.OP_READ, handler);
            System.out.println("Accepted connection from " + clientChannel.getRemoteAddress());
        } catch (IOException e) {
            throw new UncheckedIOException("IOException was thrown while accepting connection", e);
        }
    }

    private void read(SelectionKey key) {
        ConnectionHandler dispatcher = (ConnectionHandler) key.attachment();
        dispatcher.handleRead(key);
    }

    private void write(SelectionKey key) {
        ConnectionHandler dispatcher = (ConnectionHandler) key.attachment();
        dispatcher.handleWrite(key);
    }

    private void handleReadyTcpResponseWriters() {
        for (TcpResponseWriter writer : executor.drainReadyResponseWriters()) {
            SocketChannel clientChannel = writer.getClientChannel();
            clientChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
        }
    }

    public void stop() {
        isRunning = false;
        if (selector != null && selector.isOpen()) {
            selector.wakeup();
        }
    }
}
