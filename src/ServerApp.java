import tcp.server.TcpServer;

static int PORT = 3000;

void main() {
    new TcpServer(PORT).start();
}
