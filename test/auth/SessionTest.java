package auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionTest {
    @Test
    void testLoginWithValidUsername() {
        Session session = new Session();
        session.setLoggedUsername("username");

        assertTrue(session.isLogged(), "expected to have a logged user");
        assertEquals(
            "username",
            session.getUsername(),
            "the actual logged user is different from the expected"
        );
    }

    @Test
    void testLoginWithNullUsername() {
        Session session = new Session();

        assertThrows(
            IllegalArgumentException.class,
            () -> session.setLoggedUsername(null),
            "Expected IllegalArgumentException when username is null or blank"
        );
    }

    @Test
    void testLoginWithBlankUsername() {
        Session session = new Session();

        assertThrows(
            IllegalArgumentException.class,
            () -> session.setLoggedUsername(""),
            "Expected IllegalArgumentException when username is null or blank"
        );
    }

    @Test
    void testLogout() {
        Session session = new Session();
        session.setLoggedUsername("username");
        session.logout();
        assertFalse(session.isLogged(), "expected to not have a logged user");
    }
}
