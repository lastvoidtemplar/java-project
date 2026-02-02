package command;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class CommandTest {
    @Test
    void testCommandWithNullCommandName() {
        assertThrows(IllegalArgumentException.class, () -> new Command(null, List.of()),
            "Expected IllegalArgumentException when commandName is null or blank");
    }

    @Test
    void testCommandWithBlankCommandName() {
        assertThrows(IllegalArgumentException.class, () -> new Command("", List.of()),
            "Expected IllegalArgumentException when commandName is null or blank");
    }

    @Test
    void testCommandWithNullArguments() {
        assertThrows(IllegalArgumentException.class, () -> new Command("echo", null),
            "Expected IllegalArgumentException when arguments are null");
    }
}
