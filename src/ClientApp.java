final int BUFFER_SIZE = 16 * 1024;

void main() throws Exception{
    List<String> cmds = List.of(
        "login деян парола",
        "upload пейзаш.png ./resource/landscape.png",
        "quantize пейзаш.png 10 4",
        "download пейзаш_10k.png ./resource/landscape_10k.png",
        "logout"
    );

    ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
    for (String cmd: cmds) {
        putStringIntoByteBuffer(buf, cmd);
    }

    SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress("localhost", 3000));
    buf.flip();
    clientChannel.write(buf);

    Thread.sleep(2000);

    byte[] finalCmdPart1 = "login".getBytes(StandardCharsets.UTF_8);
    byte[] finalCmdPart2 = " деян ".getBytes(StandardCharsets.UTF_8);
    byte[] finalCmdPart3 = "парола".getBytes(StandardCharsets.UTF_8);

    buf.clear();
    buf.putInt(finalCmdPart1.length + finalCmdPart2.length + finalCmdPart3.length);
    buf.flip();
    clientChannel.write(buf);
    Thread.sleep(500);
    buf.clear();
    buf.put(finalCmdPart1);
    buf.flip();
    clientChannel.write(buf);
    Thread.sleep(500);
    buf.clear();
    buf.put(finalCmdPart2);
    buf.flip();
    clientChannel.write(buf);
    Thread.sleep(500);
    buf.clear();
    buf.put(finalCmdPart3);
    buf.flip();
    clientChannel.write(buf);

//    clientChannel.shutdownInput();
//    clientChannel.shutdownOutput();
    clientChannel.close();
}

void putStringIntoByteBuffer(ByteBuffer buf, String cmd) {
    byte[] bytes = cmd.getBytes(StandardCharsets.UTF_8);
    buf.putInt(bytes.length);
    buf.put(bytes);
}