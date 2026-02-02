package auth;

import command.Command;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {
    @Test
    void testUserWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> new User(null, "pass"),
            "Expected IllegalArgumentException when username is null or blank");
    }

    @Test
    void testUserWithBlankName() {
        assertThrows(IllegalArgumentException.class, () -> new User("", "pass"),
            "Expected IllegalArgumentException when username is null or blank");
    }

    @Test
    void testUserWithNullPassword() {
        assertThrows(IllegalArgumentException.class, () -> new User("username", null),
            "Expected IllegalArgumentException when password is null or blank");
    }

    @Test
    void testUserWithBlankPassword() {
        assertThrows(IllegalArgumentException.class, () -> new User("username", ""),
            "Expected IllegalArgumentException when password is null or blank");
    }

    @Test
    void testUserFromLineWithValidLine() {
        User user = User.of("username pass");

        assertEquals(
            "username",
            user.name(),
            "the actual username is different from the expected"
        );

        assertEquals(
            "pass",
            user.password(),
            "the actual password is different from the expected"
        );
    }

    @Test
    void testUserFromLineWithInvalidLine() {
        assertThrows(IllegalArgumentException.class, () -> User.of("username"),
            "Expected IllegalArgumentException when line doesn't contain 2 strings");
    }
}
