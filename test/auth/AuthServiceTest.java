package auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthServiceTest {
    private static final String USERS_FILE = "users.txt";

    private Path usersPath;

    @BeforeEach
    void setupUsersFile(@TempDir Path tempDir) throws IOException {
        Path usersPath = tempDir.resolve(USERS_FILE);
        Files.createFile(usersPath);
        this.usersPath = usersPath;
    }

    @Test
    void testRegisterWithValidInput() throws UserAlreadyExistsException, UserPersistenceException, IOException {
        AuthService authService = new AuthService(new UserFileStorage(usersPath));
        authService.register(new User("user", "pass"));
        String usersContent = Files.readString(usersPath, StandardCharsets.UTF_8);
        assertEquals(
            "user pass\n",
            usersContent,
            "incorrect user save"
        );
    }

    @Test
    void testRegisterWithExistingUser() throws UserPersistenceException, IOException {
        Files.writeString(usersPath, "user pass\n");
        AuthService authService = new AuthService(new UserFileStorage(usersPath));

        assertThrows(
            UserAlreadyExistsException.class,
            () -> authService.register(new User("user", "pass")),
            "Expected UserAlreadyExistsException when user already is registered"
        );
    }

    @Test
    void testLoginWithNotExistingUser() throws UserPersistenceException, IOException {
        Files.writeString(usersPath, "user1 pass1\n");
        AuthService authService = new AuthService(new UserFileStorage(usersPath));

        assertThrows(
            UserNotFoundException.class,
            () -> authService.login(new User("user2", "pass2")),
            "Expected UserNotFoundException when user is not found"
        );
    }

    @Test
    void testLoginWithWrongPassword() throws UserPersistenceException, IOException {
        Files.writeString(usersPath, "user1 pass1\n");
        AuthService authService = new AuthService(new UserFileStorage(usersPath));

        assertThrows(
            UserInvalidPasswordException.class,
            () -> authService.login(new User("user1", "pass2")),
            "Expected UserInvalidPasswordException when password is not wrong"
        );
    }

    @Test
    void testLoginWithValid()
        throws UserNotFoundException, UserInvalidPasswordException, UserPersistenceException, IOException {
        Files.writeString(usersPath, "user1 pass1\n");
        AuthService authService = new AuthService(new UserFileStorage(usersPath));

        assertEquals(
            "user1",
            authService.login(new User("user1", "pass1")).getUsername(),
            "expected logged in user"
        );
    }
}

