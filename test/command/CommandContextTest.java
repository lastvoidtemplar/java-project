package command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class CommandContextTest {
    @Test
    void testCommandContextWithNullCommand() {
        assertThrows(IllegalArgumentException.class, () -> new CommandContext(null, mock(), mock(), mock()),
            "Expected IllegalArgumentException when command is null");
    }

    @Test
    void testCommandContextWithNullStream() {
        assertThrows(IllegalArgumentException.class, () -> new CommandContext(mock(), null, mock(), mock()),
            "Expected IllegalArgumentException when stream is null");
    }

    @Test
    void testCommandContextWithNullSession() {
        assertThrows(IllegalArgumentException.class, () -> new CommandContext(mock(), mock(), null, mock()),
            "Expected IllegalArgumentException when session is null");
    }

    @Test
    void testCommandContextWithNullServices() {
        assertThrows(IllegalArgumentException.class, () -> new CommandContext(mock(), mock(), mock(), null),
            "Expected IllegalArgumentException when services is null");
    }
}
