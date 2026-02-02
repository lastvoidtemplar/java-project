package auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserFileStorageTest {
    private static final String USERS_FILE = "users.txt";

    private Path usersPath;

    @BeforeEach
    void setupUsersFile(@TempDir Path tempDir) throws IOException {
        Path usersPath = tempDir.resolve(USERS_FILE);
        Files.createFile(usersPath);
        this.usersPath = usersPath;
    }

    @Test
    void testUserFileStorageWithNullPath() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new UserFileStorage(null),
            "Expected IllegalArgumentException when path is null"
        );
    }

    @Test
    void testLoadUsersWithValidUsersFile() throws UserPersistenceException, IOException {
        Files.writeString(usersPath, "user1 pass1\nuser2 pass2\nuser3 pass3");
        List<User> actualUsers = new UserFileStorage(usersPath).loadUsers();
        List<User> expectedUsers = List.of(
            new User("user1", "pass1"),
            new User("user2", "pass2"),
            new User("user3", "pass3")
        );
        assertIterableEquals(
            expectedUsers, actualUsers,
            "difference between the actual users and expected users");
    }

    @Test
    void testLoadUsersWithInvalidUsersFile() throws IOException {
        Files.writeString(usersPath, "user1 pass1\nuser2 pass2\nuser3pass3");

        assertThrows(
            UserPersistenceException.class,
            () ->  new UserFileStorage(usersPath).loadUsers(),
            "Expected UserPersistenceException when users.txt is not valid"
        );
    }

    @Test
    void testSaveUserWithNullUser() throws IOException {
        assertThrows(
            IllegalArgumentException.class,
            () ->  new UserFileStorage(usersPath).saveUser(null),
            "Expected IllegalArgumentException when user is null"
        );
    }

    @Test
    void testSaveUserWithValid() throws UserPersistenceException, IOException {
        new UserFileStorage(usersPath).saveUser(new User("user", "pass"));
        String usersContent = Files.readString(usersPath, StandardCharsets.UTF_8);
        assertEquals(
            "user pass\n",
            usersContent,
            "incorrect user save"
        );
    }
}
