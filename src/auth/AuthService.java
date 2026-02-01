package auth;

import java.util.Map;
import java.util.stream.Collectors;

public class AuthService {
    private static final String USER_ALREADY_EXISTS_MESSAGE = "User with this name already exists";
    private static final String USER_NOT_FOUND_MESSAGE = "User with this name does not exists";
    private static final String INVALID_PASSWORD_MESSAGE = "Invalid password";

    private final Map<String, String> users;
    private final UserFileStorage storage;

    public AuthService(UserFileStorage storage) throws UserPersistenceException {
        this.storage = storage;
        this.users = storage.loadUsers().stream().collect(Collectors.toMap(User::name, User::password));
    }

    public void register(User user) throws UserPersistenceException, UserAlreadyExistsException {
        if (users.containsKey(user.name())) {
            throw new UserAlreadyExistsException(USER_ALREADY_EXISTS_MESSAGE);
        }
        storage.saveUser(user);
        users.put(user.name(), user.password());
    }

    public Session login(User user) throws UserNotFoundException, UserInvalidPasswordException {
        if (!users.containsKey(user.name())) {
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
        }
        if (!users.get(user.name()).equals(user.password())) {
            throw new UserInvalidPasswordException(INVALID_PASSWORD_MESSAGE);
        }
        Session session = new Session();
        session.setLoggedUsername(user.name());
        return session;
    }
}
