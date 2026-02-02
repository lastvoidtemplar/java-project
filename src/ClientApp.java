import tcp.utils.BoundedChannelInputStream;

import javax.xml.transform.Source;

final int BUFFER_SIZE = 16 * 1024;

final List<String> cmds = List.of(
    "login ivan 1234",
    "whoami",
    "logout",
    "whoami"
);

void main() throws Exception {
    SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress("localhost", 3000));
    ByteBuffer buf = ByteBuffer.allocate(1024);

    putStringIntoByteBuffer(buf, "login ivan 4321");
    buf.putInt(0);
    buf.flip();
    clientChannel.write(buf);
    buf.clear();
    clientChannel.read(buf);
    buf.clear();

    putStringIntoByteBuffer(buf, "download landscape2.jpg");
    buf.putInt(0);
    buf.flip();
    clientChannel.write(buf);
    buf.clear();
    Path path = Path.of("./resources/downloaded_landscape.jpg");
    Thread.sleep(1000);
    downLoadFile(clientChannel, buf, path);
}

void sendCommands(SocketChannel clientChannel, ByteBuffer buf) throws IOException {
    for (String cmd : cmds) {
        putStringIntoByteBuffer(buf, cmd);
    }
    buf.flip();
    clientChannel.write(buf);
    buf.clear();
}

void putStringIntoByteBuffer(ByteBuffer buf, String cmd) {
    byte[] bytes = cmd.getBytes(StandardCharsets.UTF_8);
    buf.putInt(bytes.length);
    buf.put(bytes);
}

private void handleSelectedKeys(Selector selector) throws IOException {
    var selectedKeys = selector.selectedKeys().iterator();
    while (selectedKeys.hasNext()) {
        SelectionKey key = selectedKeys.next();
        selectedKeys.remove();

        if (key.isReadable()) {
            read(key);
        }
    }
}

private void read(SelectionKey key) throws IOException {
    SocketChannel clientChannel = (SocketChannel) key.channel();
    ByteBuffer buf = (ByteBuffer) key.attachment();
    clientChannel.read(buf);
    buf.flip();

    while (buf.remaining() >= Integer.SIZE / Byte.SIZE) {
        writeToStdout(buf);
    }

    buf.compact();
}

private void writeToStdout(ByteBuffer buf) throws IOException {

    int msgLen = buf.getInt();
    int originalLimit = buf.limit();
    buf.limit(buf.position() + msgLen);
    Channels.newChannel(System.out).write(buf);
    System.out.println();
    buf.limit(originalLimit);
}

private void sendFile(SocketChannel clientChannel, ByteBuffer buf, Path path) throws IOException {
    System.out.println(Files.size(path));
    buf.putInt((int) Files.size(path));
    buf.flip();
    clientChannel.write(buf);
    buf.clear();
    try (FileChannel file = FileChannel.open(path)) {
        while (file.read(buf) >= 0) {
            buf.flip();
            clientChannel.write(buf);
            buf.compact();
        }
    }
}

private void downLoadFile(SocketChannel clientChannel, ByteBuffer buf, Path path) throws IOException {
    clientChannel.read(buf);
    buf.flip();
    int messageSize = buf.getInt();
    byte[] resBytes = new byte[messageSize];
    buf.get(resBytes);
    String res = new String(resBytes, StandardCharsets.UTF_8);
    System.out.println(res);
    if (res.startsWith("error")) {
        System.out.println("ERROR");
        return;
    }
    int fileSize = buf.getInt();
    try (InputStream socketStream = new BoundedChannelInputStream(clientChannel, buf, fileSize)) {
        Files.copy(socketStream, path);
    }
}