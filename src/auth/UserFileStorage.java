package auth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Formatter;
import java.util.List;

public class UserFileStorage {
    private static final String PATH_NULL_MESSAGE = "Path must be non-null";
    private static final String USER_NULL_MESSAGE = "User must be non-null";
    private static final String SAVE_USER_FORMAT = "%s %s" + System.lineSeparator();
    private final Path path;

    public UserFileStorage(Path path) {
        if (path == null) {
            throw new IllegalArgumentException(PATH_NULL_MESSAGE);
        }
        this.path = path;
    }

    public synchronized List<User> loadUsers() throws UserPersistenceException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            return reader.lines().map(User::of).toList();
        } catch (IOException | IllegalArgumentException e) {
            throw new UserPersistenceException("Couldn`t load users", e);
        }
    }

    public synchronized void saveUser(User user) throws UserPersistenceException {
        if (user == null) {
            throw new IllegalArgumentException(USER_NULL_MESSAGE);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), true))) {
            Formatter fmt = new Formatter();
            String line = fmt.format(SAVE_USER_FORMAT, user.name(), user.password()).toString();
            writer.write(line);
        } catch (IOException | IllegalArgumentException e) {
            throw new UserPersistenceException("Couldn`t load users", e);
        }
    }
}
