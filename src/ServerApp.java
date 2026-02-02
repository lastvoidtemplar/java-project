import auth.AuthService;
import auth.UserFileStorage;
import auth.UserPersistenceException;
import dispatcher.Dispatcher;
import handlers.EchoHandler;
import handlers.StreamHandler;
import handlers.auth.LoginHandler;
import handlers.auth.LogoutHandler;
import handlers.auth.RegisterHandler;
import handlers.auth.WhoAmIHandler;
import services.Services;
import tcp.server.TcpServer;

static int PORT = 3000;
static String USERS_FILE_NAME = "./resources/users.txt";

void main() throws Exception {
    Services services = setupServices();
    Dispatcher dispatcher = setupDispatcher();
    new TcpServer(PORT, services, dispatcher).start();
}

private Services setupServices() throws UserPersistenceException {
    UserFileStorage userStorage = new UserFileStorage(Path.of(USERS_FILE_NAME));
    AuthService authService = new AuthService(userStorage);
    return new Services(authService);
}

private Dispatcher setupDispatcher() {
    return Dispatcher.newBuilder()
        .registerHandler("echo", new EchoHandler())
        .registerHandler("stream", new StreamHandler())
        .registerHandler("register", new RegisterHandler())
        .registerHandler("login", new LoginHandler())
        .registerHandler("logout", new LogoutHandler())
        .registerHandler("whoami", new WhoAmIHandler())
        .build();
}
