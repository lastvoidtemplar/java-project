import tcp.utils.BoundedChannelInputStream;

final int BUFFER_SIZE = 8 * 1024;
final String HOST = "localhost";
final int PORT = 3000;
final int INT_SIZE = 4;
final int DOWNLOAD_MESSAGE_LEN = 7;
final String EXIT = "exit";

void main() {
    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    try (
        SocketChannel clientSocket = SocketChannel.open(new InetSocketAddress(HOST, PORT));
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in))
    ) {
        String line;
        while ((line = stdin.readLine()) != null) {
            if (line.isBlank()) {
                continue;
            }
            String[] splitLine = line.split(" ");
            if (splitLine[0].equals(EXIT)) {
                return;
            }
            dispatch(splitLine, clientSocket, buffer);
        }
    } catch (IOException e) {
        // there isn't much we can do when a IOException happens
        throw new UncheckedIOException(e);
    }
}

void dispatch(String[] splitLine, SocketChannel clientSocket, ByteBuffer buffer) throws IOException {
    switch (splitLine[0]) {
        case "register", "login", "logout", "whoami", "list-files", "delete-file", "color-quantize" ->
            handleLineAndMessageResponse(splitLine, clientSocket, buffer);
        case "upload" -> handleUpload(splitLine, clientSocket, buffer);
        case "download" -> handleDownload(splitLine, clientSocket, buffer);
        default -> System.out.println("Invalid command");
    }
}

void handleLineAndMessageResponse(String[] splitLine, SocketChannel clientSocket, ByteBuffer buffer)
    throws IOException {
    String cmd = String.join(" ", Arrays.asList(splitLine));
    putStringIntoByteBuffer(buffer, cmd);
    putNullStreamIntoByteBuffer(buffer);
    buffer.flip();
    while (buffer.hasRemaining()) {
        clientSocket.write(buffer);
    }
    buffer.clear();

    String response = readResponseMessage(clientSocket, buffer);
    buffer.clear();
    System.out.println(response);
}

void handleUpload(String[] splitLine, SocketChannel clientSocket, ByteBuffer buffer)
    throws IOException {
    Path filePath = Path.of(splitLine[splitLine.length - 1]);
    if (!Files.exists(filePath)) {
        System.out.println("File doesn't exists");
        return;
    }

    String cmd = String.join(" ", Arrays.asList(splitLine).subList(0, splitLine.length - 1));
    putStringIntoByteBuffer(buffer, cmd);
    buffer.flip();
    while (buffer.hasRemaining()) {
        clientSocket.write(buffer);
    }
    buffer.clear();

    sendFile(clientSocket, buffer, filePath);
    buffer.clear();
    String response = readResponseMessage(clientSocket, buffer);
    buffer.clear();
    System.out.println(response);
}

void handleDownload(String[] splitLine, SocketChannel clientSocket, ByteBuffer buffer)
    throws IOException {
    String cmd = String.join(" ", Arrays.asList(splitLine).subList(0, splitLine.length - 1));
    putStringIntoByteBuffer(buffer, cmd);
    putNullStreamIntoByteBuffer(buffer);
    buffer.flip();
    while (buffer.hasRemaining()) {
        clientSocket.write(buffer);
    }
    buffer.clear();
    readDownloadResponse(clientSocket, buffer, Path.of(splitLine[splitLine.length - 1]));
    buffer.clear();
}


void putStringIntoByteBuffer(ByteBuffer buf, String cmd) {
    byte[] bytes = cmd.getBytes(StandardCharsets.UTF_8);
    buf.putInt(bytes.length);
    buf.put(bytes);
}

void putNullStreamIntoByteBuffer(ByteBuffer buf) {
    buf.putInt(0);
}

String readResponseMessage(SocketChannel clientChannel, ByteBuffer buffer) throws IOException {
    while (buffer.position() < INT_SIZE) {
        clientChannel.read(buffer);
    }
    buffer.flip();
    int messageLen = buffer.getInt();
    buffer.compact();
    while (buffer.position() < messageLen) {
        clientChannel.read(buffer);
    }
    buffer.flip();
    byte[] messageBytes = new byte[messageLen];
    buffer.get(messageBytes, 0, messageLen);
    return new String(messageBytes, StandardCharsets.UTF_8);
}

private void sendFile(SocketChannel clientChannel, ByteBuffer buffer, Path path) throws IOException {
    buffer.putInt((int) Files.size(path));
    buffer.flip();
    while (buffer.hasRemaining()) {
        clientChannel.write(buffer);
    }
    buffer.clear();
    try (FileChannel file = FileChannel.open(path)) {
        while (file.read(buffer) >= 0) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                clientChannel.write(buffer);
            }
            buffer.compact();
        }
    }
}

private void readDownloadResponse(SocketChannel clientChannel, ByteBuffer buffer, Path path) throws IOException {
    while (buffer.position() < INT_SIZE) {
        clientChannel.read(buffer);
    }
    buffer.flip();
    int messageSize = buffer.getInt();
    buffer.compact();
    if (messageSize < DOWNLOAD_MESSAGE_LEN) {
        return;
    }
    while (buffer.position() < DOWNLOAD_MESSAGE_LEN) {
        clientChannel.read(buffer);
    }
    buffer.flip();
    byte[] statusBytes = new byte[DOWNLOAD_MESSAGE_LEN];
    buffer.get(statusBytes, 0, DOWNLOAD_MESSAGE_LEN);
    String status = new String(statusBytes);

    switch (status) {
        case "correct" -> downloadFile(clientChannel, buffer, path, messageSize - DOWNLOAD_MESSAGE_LEN);
        case "error  " -> readDownloadMessage(clientChannel, buffer, messageSize - DOWNLOAD_MESSAGE_LEN);
    }
}

void readDownloadMessage(SocketChannel clientChannel, ByteBuffer buffer, int messageLen) throws IOException {
    buffer.compact();
    while (buffer.position() < messageLen ) {
        clientChannel.read(buffer);
    }
    buffer.flip();
    byte[] messageBytes = new byte[messageLen];
    buffer.get(messageBytes, 0, messageLen);
    String message= new String(messageBytes, StandardCharsets.UTF_8);
    System.out.println(message);
}

void downloadFile(SocketChannel clientChannel, ByteBuffer buffer, Path filePath, int fileSize) throws IOException {
    try (InputStream socketStream = new BoundedChannelInputStream(clientChannel, buffer, fileSize)) {
        Files.copy(socketStream, filePath, StandardCopyOption.REPLACE_EXISTING);
    }
    System.out.println("Download successful");
}