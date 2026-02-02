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
import handlers.user.storage.DownloadHandler;
import handlers.user.storage.UploadHandler;
import services.Services;
import tcp.server.TcpServer;
import user.storage.UserDirectoryService;

static int PORT = 3000;
static String USERS_FILE_NAME = "./resources/users.txt";
static String ROOT_USER_DIRECTORY = "./resources/users_files";

void main() throws Exception {
    Services services = setupServices();
    Dispatcher dispatcher = setupDispatcher();
    new TcpServer(PORT, services, dispatcher).start();
}

private Services setupServices() throws UserPersistenceException {
    UserFileStorage userStorage = new UserFileStorage(Path.of(USERS_FILE_NAME));
    AuthService authService = new AuthService(userStorage);

    UserDirectoryService userDirectoryService = new UserDirectoryService(Path.of(ROOT_USER_DIRECTORY));

    return new Services(authService, userDirectoryService);
}

private Dispatcher setupDispatcher() {
    return Dispatcher.newBuilder()
        .registerHandler("echo", new EchoHandler())
        .registerHandler("stream", new StreamHandler())
        .registerHandler("register", new RegisterHandler())
        .registerHandler("login", new LoginHandler())
        .registerHandler("logout", new LogoutHandler())
        .registerHandler("whoami", new WhoAmIHandler())
        .registerHandler("upload", new UploadHandler())
        .registerHandler("download", new DownloadHandler())
        .build();
}
